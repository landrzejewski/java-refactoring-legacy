package pl.training.workshop.m8.s09_codemod.step2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * Krok 2: przepisanie na podstawie pozycji z AST - podmieniamy dokładnie dwa ostatnie argumenty
 * (literał -> stała enum, wyrażenie -> operator warunkowy) i dopisujemy importy. Formatowanie
 * reszty pliku zostaje. Nadal tylko składnia: HotelService.book też zostanie "zmigrowany".
 */
public final class BookCallCodemod {
    private record Edit(int start, int end, String text) {
    }

    private record Parsed(CompilationUnitTree unit, SourcePositions positions,
            List<MethodInvocationTree> calls) {
    }

    /** Źródła projektu (API) - samo parsowanie składni ich nie potrzebuje. */
    public BookCallCodemod(List<String> projectSources) {
    }

    public List<Integer> findLines(String source) {
        Parsed parsed = parse(source);
        return parsed.calls().stream()
                .map(call -> (int) parsed.unit().getLineMap()
                        .getLineNumber(parsed.positions().getStartPosition(parsed.unit(), call)))
                .toList();
    }

    public String rewrite(String source) {
        Parsed parsed = parse(source);
        List<Edit> edits = new ArrayList<>();
        for (MethodInvocationTree call : parsed.calls()) {
            List<? extends ExpressionTree> args = call.getArguments();
            edits.add(replace(parsed, source, args.get(4), "Channel.WEB", "Channel.BOX_OFFICE"));
            edits.add(replace(parsed, source, args.get(5), "Glasses.OWN", "Glasses.RENTED"));
        }
        if (edits.isEmpty()) {
            return source;
        }
        edits.add(imports(parsed, source));
        StringBuilder result = new StringBuilder(source);
        edits.stream().sorted(Comparator.comparingInt(Edit::start).reversed())
                .forEach(edit -> result.replace(edit.start(), edit.end(), edit.text()));
        return result.toString();
    }

    private static Edit replace(Parsed parsed, String source, ExpressionTree arg,
            String ifTrue, String ifFalse) {
        int start = (int) parsed.positions().getStartPosition(parsed.unit(), arg);
        int end = (int) parsed.positions().getEndPosition(parsed.unit(), arg);
        if (arg instanceof LiteralTree literal && literal.getValue() instanceof Boolean value) {
            return new Edit(start, end, value ? ifTrue : ifFalse);
        }
        String text = source.substring(start, end);
        boolean simple = arg instanceof IdentifierTree || arg instanceof MemberSelectTree
                || arg instanceof MethodInvocationTree;
        return new Edit(start, end, (simple ? text : "(" + text + ")") + " ? " + ifTrue + " : " + ifFalse);
    }

    private static Edit imports(Parsed parsed, String source) {
        StringBuilder missing = new StringBuilder();
        for (String type : List.of("cinema.Channel", "cinema.Glasses")) {
            if (!source.contains("import " + type + ";")) {
                missing.append("\nimport ").append(type).append(';');
            }
        }
        List<? extends ImportTree> imports = parsed.unit().getImports();
        int at = (int) parsed.positions().getEndPosition(parsed.unit(), imports.getLast());
        return new Edit(at, at, missing.toString());
    }

    private static Parsed parse(String source) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        URI uri = URI.create("string:///Source.java");
        JavaFileObject file = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        JavacTask task = (JavacTask) compiler.getTask(null, null, diagnostic -> { }, List.of("-proc:none"),
                null, List.of(file));
        try {
            CompilationUnitTree unit = task.parse().iterator().next();
            List<MethodInvocationTree> calls = new ArrayList<>();
            new TreeScanner<Void, Void>() {
                @Override
                public Void visitMethodInvocation(MethodInvocationTree call, Void unused) {
                    if (looksLikeOldBook(call)) {
                        calls.add(call);
                    }
                    return super.visitMethodInvocation(call, unused);
                }
            }.scan(unit, null);
            return new Parsed(unit, Trees.instance(task).getSourcePositions(), calls);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Składnia: metoda o nazwie book z sześcioma argumentami. Typu odbiorcy nie znamy. */
    private static boolean looksLikeOldBook(MethodInvocationTree call) {
        return call.getMethodSelect() instanceof MemberSelectTree select
                && select.getIdentifier().contentEquals("book")
                && call.getArguments().size() == 6;
    }
}
