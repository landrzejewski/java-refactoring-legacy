package pl.training.workshop.m5.s13_sealed;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.training.workshop.shared.Money;

/** Otwarta hierarchia kontra sealed: cichy błąd, błąd kompilacji i MatchException w starym binarium. */
final class S13SolutionTest {
    private static final Path SOURCES = Path.of("src/main/java/pl/training/workshop/m5/s13_sealed");

    /**
     * Pułapka otwartej hierarchii: typ dopisany "z zewnątrz" (tu w runtime przez Proxy, żeby test
     * kompilował się także po zapieczętowaniu start na żywo) dostaje po cichu 0% zniżki.
     * Po kroku 1 wykonanym na start ten test zrobi się czerwony - sealed nie wpuści obcego typu.
     */
    @Test
    void startSilentlyGivesUnknownTicketNoDiscount() {
        var child = (pl.training.workshop.m5.s13_sealed.start.Ticket) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {pl.training.workshop.m5.s13_sealed.start.Ticket.class}, (proxy, method, args) -> Money.of("25.00"));
        assertEquals(Money.of("25.00"), new pl.training.workshop.m5.s13_sealed.start.PriceCalculator().price(child),
                "pułapka: bilet dziecięcy (40%) policzony jak normalny");
    }

    @Test
    void sealedHierarchyListsAllVariants() {
        assertTrue(pl.training.workshop.m5.s13_sealed.step1.Ticket.class.isSealed());
        assertArrayEquals(new Class<?>[] {
                pl.training.workshop.m5.s13_sealed.step1.StandardTicket.class, pl.training.workshop.m5.s13_sealed.step1.StudentTicket.class, pl.training.workshop.m5.s13_sealed.step1.SeniorTicket.class},
                pl.training.workshop.m5.s13_sealed.step1.Ticket.class.getPermittedSubclasses());
    }

    @Test
    void solutionHandlesChildTicket() {
        assertEquals(Money.of("15.00"),
                new pl.training.workshop.m5.s13_sealed.step3.PriceCalculator().price(new pl.training.workshop.m5.s13_sealed.step3.ChildTicket(Money.of("25.00"))));
    }

    /**
     * Zgodność binarna sealed: PriceCalculator skompilowany przeciw step2 (3 warianty) uruchomiony
     * z hierarchią ze step3 (4 warianty). Linkuje się bez błędu, ale switch rzuca MatchException.
     */
    @Test
    void oldExhaustiveSwitchThrowsMatchExceptionForNewVariant(@TempDir Path tmp) throws Exception {
        Path v1 = compile("step2", List.of(), tmp.resolve("v1"));
        Path v2 = compile("step3", List.of("PriceCalculator.java"), tmp.resolve("v2"));
        Files.copy(v1.resolve("api/PriceCalculator.class"), v2.resolve("api/PriceCalculator.class"),
                StandardCopyOption.REPLACE_EXISTING);
        try (URLClassLoader loader = new URLClassLoader(new URL[] {v2.toUri().toURL()}, getClass().getClassLoader())) {
            Object calculator = loader.loadClass("api.PriceCalculator").getConstructor().newInstance();
            Class<?> ticketType = loader.loadClass("api.Ticket");
            Object child = loader.loadClass("api.ChildTicket").getConstructor(Money.class)
                    .newInstance(Money.of("25.00"));
            var call = calculator.getClass().getMethod("discountPercent", ticketType);
            InvocationTargetException failure = assertThrows(InvocationTargetException.class,
                    () -> call.invoke(calculator, child));
            assertInstanceOf(MatchException.class, failure.getCause());
        }
    }

    /** Kompiluje pliki wariantu sceny, podmieniając pakiet na "api" (ta sama nazwa klas w v1 i v2). */
    private static Path compile(String variant, List<String> skip, Path out) throws Exception {
        Path src = out.resolveSibling(out.getFileName() + "-src/api");
        Files.createDirectories(src);
        Files.createDirectories(out);
        List<Path> files = new ArrayList<>();
        try (var stream = Files.list(SOURCES.resolve(variant))) {
            for (Path file : stream.toList()) {
                if (skip.contains(file.getFileName().toString())) {
                    continue;
                }
                Path target = src.resolve(file.getFileName());
                Files.writeString(target, Files.readString(file).replace("pl.training.workshop.m5.s13_sealed." + variant, "api"));
                files.add(target);
            }
        }
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fm = javac.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> units = fm.getJavaFileObjectsFromPaths(files);
            String classpath = Path.of(Money.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toString();
            boolean ok = javac.getTask(null, fm, null, List.of("-d", out.toString(), "-cp", classpath),
                    null, units).call();
            assertTrue(ok, "kompilacja " + variant);
        }
        return out;
    }
}
