package pl.training.workshop.m8.s09_codemod.step3;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

/**
 * Krok 3: dopasowanie po typach, nie po nazwie. Kod jest analizowany razem ze źródłami projektu,
 * a wywołanie trafia do migracji tylko wtedy, gdy kompilator rozwiązał je do przestarzałej
 * metody cinema.BookingService.book. HotelService zostaje w spokoju, a ponowne uruchomienie
 * na zmigrowanym kodzie niczego nie zmienia (idempotencja).
 */
public final class BookCallCodemod {
    private static final String OLD_API_OWNER = "cinema.BookingService";

    private record Edit(int start, int end, String text) {
    }

    private record Analyzed(CompilationUnitTree unit, SourcePositions positions,
            List<MethodInvocationTree> calls) {
    }

    private final List<String> projectSources;

    public BookCallCodemod(List<String> projectSources) {
        this.projectSources = List.copyOf(projectSources);
    }

    public List<Integer> findLines(String source) {
        Analyzed analyzed = analyze(source);
        return analyzed.calls().stream()
                .map(call -> (int) analyzed.unit().getLineMap()
                        .getLineNumber(analyzed.positions().getStartPosition(analyzed.unit(), call)))
                .toList();
    }

    public String rewrite(String source) {
        Analyzed analyzed = analyze(source);
        List<Edit> edits = new ArrayList<>();
        for (MethodInvocationTree call : analyzed.calls()) {
            List<? extends ExpressionTree> args = call.getArguments();
            edits.add(replace(analyzed, source, args.get(4), "Channel.WEB", "Channel.BOX_OFFICE"));
            edits.add(replace(analyzed, source, args.get(5), "Glasses.OWN", "Glasses.RENTED"));
        }
        if (edits.isEmpty()) {
            return source;
        }
        edits.add(imports(analyzed, source));
        StringBuilder result = new StringBuilder(source);
        edits.stream().sorted(Comparator.comparingInt(Edit::start).reversed())
                .forEach(edit -> result.replace(edit.start(), edit.end(), edit.text()));
        return result.toString();
    }

    private static Edit replace(Analyzed analyzed, String source, ExpressionTree arg,
            String ifTrue, String ifFalse) {
        int start = (int) analyzed.positions().getStartPosition(analyzed.unit(), arg);
        int end = (int) analyzed.positions().getEndPosition(analyzed.unit(), arg);
        if (arg instanceof LiteralTree literal && literal.getValue() instanceof Boolean value) {
            return new Edit(start, end, value ? ifTrue : ifFalse);
        }
        String text = source.substring(start, end);
        boolean simple = arg instanceof IdentifierTree || arg instanceof MemberSelectTree
                || arg instanceof MethodInvocationTree;
        return new Edit(start, end, (simple ? text : "(" + text + ")") + " ? " + ifTrue + " : " + ifFalse);
    }

    private static Edit imports(Analyzed analyzed, String source) {
        StringBuilder missing = new StringBuilder();
        for (String type : List.of("cinema.Channel", "cinema.Glasses")) {
            if (!source.contains("import " + type + ";")) {
                missing.append("\nimport ").append(type).append(';');
            }
        }
        List<? extends ImportTree> imports = analyzed.unit().getImports();
        int at = (int) analyzed.positions().getEndPosition(analyzed.unit(), imports.getLast());
        return new Edit(at, at, missing.toString());
    }

    private Analyzed analyze(String source) {
        List<JavaFileObject> files = new ArrayList<>();
        projectSources.forEach(api -> files.add(file(api)));
        files.add(file(source));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavacTask task = (JavacTask) compiler.getTask(null, null, diagnostic -> { }, List.of("-proc:none"),
                null, files);
        try {
            CompilationUnitTree unit = null;
            for (CompilationUnitTree parsed : task.parse()) {
                unit = parsed; // jednostki w kolejności plików - migrowany plik jest ostatni
            }
            task.analyze();
            Trees trees = Trees.instance(task);
            Elements elements = task.getElements();
            List<MethodInvocationTree> calls = new ArrayList<>();
            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitMethodInvocation(MethodInvocationTree call, Void unused) {
                    if (trees.getElement(getCurrentPath()) instanceof ExecutableElement method
                            && isOldBook(method, elements)) {
                        calls.add(call);
                    }
                    return super.visitMethodInvocation(call, unused);
                }
            }.scan(new TreePath(unit), null);
            return new Analyzed(unit, trees.getSourcePositions(), calls);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Typy: przestarzała metoda book zadeklarowana w cinema.BookingService. */
    private static boolean isOldBook(ExecutableElement method, Elements elements) {
        return method.getSimpleName().contentEquals("book")
                && elements.isDeprecated(method)
                && method.getEnclosingElement() instanceof TypeElement owner
                && owner.getQualifiedName().contentEquals(OLD_API_OWNER);
    }

    private static JavaFileObject file(String source) {
        Matcher name = Pattern.compile("public\\s+(?:class|enum|interface|record)\\s+(\\w+)").matcher(source);
        String fileName = (name.find() ? name.group(1) : "Source") + ".java";
        return new SimpleJavaFileObject(URI.create("string:///" + fileName), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
    }
}
