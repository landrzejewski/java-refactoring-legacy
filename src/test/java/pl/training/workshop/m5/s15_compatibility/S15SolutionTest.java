package pl.training.workshop.m5.s15_compatibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.training.workshop.shared.Money;

/**
 * Zgodność binarna i refleksyjna. Wtyczka partnera kompilowana jest przez javax.tools przeciw jednej
 * wersji API (pliki wariantu sceny z pakietem podmienionym na "api"), a uruchamiana z inną.
 * Wersja bazowa "1.x" to step1 (API identyczne jak w start, ale start zmieniamy na żywo).
 */
final class S15SolutionTest {
    private static final Path SOURCES = Path.of("src/main/java/pl/training/workshop/m5/s15_compatibility");

    private static final String PLUGIN = """
            package client;

            import api.BoxOfficeApi;
            import api.StudentTicket;
            import pl.training.workshop.shared.Money;

            public final class Plugin {
                public static String run() {
                    StudentTicket ticket = new StudentTicket("Amator", Money.of("25.00"));
                    return ticket.price() + "/" + new BoxOfficeApi().quote(ticket);
                }
            }
            """;

    @TempDir
    Path tmp;

    @Test
    void pullUpIsBinaryCompatibleForCallers() throws Exception {
        Path plugin = compilePlugin(compileApi("step1"));
        assertEquals("18.75/18.75", run(plugin, compileApi("step1")));
        assertEquals("18.75/18.75", run(plugin, compileApi("step2")), "StudentTicket.price() znalezione w nadklasie");
    }

    @Test
    void generalizedParameterBreaksOldBinary() throws Exception {
        Path plugin = compilePlugin(compileApi("step1"));
        InvocationTargetException failure = assertThrows(InvocationTargetException.class,
                () -> run(plugin, compileApi("step3")));
        assertInstanceOf(NoSuchMethodError.class, failure.getCause());
        assertTrue(failure.getCause().getMessage().contains("quote"));
    }

    @Test
    void generalizedParameterIsSourceCompatible() throws Exception {
        Path api = compileApi("step3");
        assertEquals("18.75/18.75", run(compilePlugin(api), api), "po rekompilacji wtyczka działa");
    }

    @Test
    void delegatingOverloadRestoresBinaryCompatibility() throws Exception {
        Path plugin = compilePlugin(compileApi("step1"));
        assertEquals("18.75/18.75", run(plugin, compileApi("step4")));
    }

    @Test
    void pullUpHidesAnnotatedMethodFromDeclaredMethodsLookup() {
        assertTrue(hasDeclaredColumn(pl.training.workshop.m5.s15_compatibility.step1.StudentTicket.class));
        assertFalse(hasDeclaredColumn(pl.training.workshop.m5.s15_compatibility.step2.StudentTicket.class),
                "pułapka: eksporter oparty na getDeclaredMethods() nie znalazłby kolumny po Pull Up");
        assertTrue(hasDeclaredColumn(pl.training.workshop.m5.s15_compatibility.step2.Ticket.class));
    }

    private static boolean hasDeclaredColumn(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods()).anyMatch(m -> m.isAnnotationPresent(Column.class));
    }

    private Path compileApi(String variant) throws Exception {
        Path src = tmp.resolve(variant + "-src/api");
        Files.createDirectories(src);
        List<Path> files = new ArrayList<>();
        try (var stream = Files.list(SOURCES.resolve(variant))) {
            for (Path file : stream.toList()) {
                Path target = src.resolve(file.getFileName());
                Files.writeString(target, Files.readString(file).replace("pl.training.workshop.m5.s15_compatibility." + variant, "api"));
                files.add(target);
            }
        }
        return compile(files, tmp.resolve(variant + "-classes"), "");
    }

    private Path compilePlugin(Path api) throws Exception {
        Path src = tmp.resolve("plugin-" + api.getFileName() + "/client/Plugin.java");
        Files.createDirectories(src.getParent());
        Files.writeString(src, PLUGIN);
        return compile(List.of(src), tmp.resolve("plugin-classes-" + api.getFileName()), api.toString());
    }

    private static Path compile(List<Path> files, Path out, String extraClasspath) throws Exception {
        Files.createDirectories(out);
        String classpath = String.join(java.io.File.pathSeparator, codeSource(Money.class), codeSource(Column.class))
                + (extraClasspath.isEmpty() ? "" : java.io.File.pathSeparator + extraClasspath);
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fm = javac.getStandardFileManager(null, null, null)) {
            boolean ok = javac.getTask(null, fm, null, List.of("-d", out.toString(), "-cp", classpath),
                    null, fm.getJavaFileObjectsFromPaths(files)).call();
            assertTrue(ok, "kompilacja " + files);
        }
        return out;
    }

    private static String codeSource(Class<?> type) throws Exception {
        return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
    }

    /** Uruchamia wtyczkę z klasami API z podanego katalogu (jak podmiana JAR-a na serwerze). */
    private String run(Path plugin, Path api) throws Exception {
        URL[] urls = {api.toUri().toURL(), plugin.toUri().toURL()};
        try (URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader())) {
            Method run = loader.loadClass("client.Plugin").getMethod("run");
            return (String) run.invoke(null);
        }
    }
}
