package pl.training.workshop.m8.s09_codemod.step1;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * Krok 1: wyszukiwanie przez AST (Compiler Tree API). Parser javac widzi wywołania metod,
 * a nie linie tekstu: komentarz przestaje być trafieniem, wywołanie na trzech liniach jest
 * znalezione. Przepisanie nadal tekstowe (bez zmian od startu).
 */
public final class BookCallCodemod {
    /** Źródła projektu (API) - samo parsowanie składni ich nie potrzebuje. */
    public BookCallCodemod(List<String> projectSources) {
    }

    public List<Integer> findLines(String source) {
        JavacTask task = task(source);
        CompilationUnitTree unit = parse(task);
        SourcePositions positions = Trees.instance(task).getSourcePositions();
        List<Integer> lines = new ArrayList<>();
        new TreeScanner<Void, Void>() {
            @Override
            public Void visitMethodInvocation(MethodInvocationTree call, Void unused) {
                if (looksLikeOldBook(call)) {
                    long start = positions.getStartPosition(unit, call);
                    lines.add((int) unit.getLineMap().getLineNumber(start));
                }
                return super.visitMethodInvocation(call, unused);
            }
        }.scan(unit, null);
        return lines;
    }

    public String rewrite(String source) {
        return source
                .replace(", true, true)", ", Channel.WEB, Glasses.OWN)")
                .replace(", true, false)", ", Channel.WEB, Glasses.RENTED)")
                .replace(", false, true)", ", Channel.BOX_OFFICE, Glasses.OWN)")
                .replace(", false, false)", ", Channel.BOX_OFFICE, Glasses.RENTED)")
                .replace("import cinema.BookingService;",
                        "import cinema.BookingService;\nimport cinema.Channel;\nimport cinema.Glasses;");
    }

    /** Składnia: metoda o nazwie book z sześcioma argumentami. Typu odbiorcy nie znamy. */
    private static boolean looksLikeOldBook(MethodInvocationTree call) {
        return call.getMethodSelect() instanceof MemberSelectTree select
                && select.getIdentifier().contentEquals("book")
                && call.getArguments().size() == 6;
    }

    private static JavacTask task(String source) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        URI uri = URI.create("string:///Source.java");
        JavaFileObject file = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        return (JavacTask) compiler.getTask(null, null, diagnostic -> { }, List.of("-proc:none"),
                null, List.of(file));
    }

    private static CompilationUnitTree parse(JavacTask task) {
        try {
            return task.parse().iterator().next();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
