package pl.training.module4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Jeden punkt wejścia zamiast wielu metod {@code main} z katalogu wzorców.
 * <ul>
 * <li>bez argumentów - wypisuje dostępne klucze przykładów,</li>
 * <li>{@code builder} - uruchamia jeden przykład,</li>
 * <li>{@code all} - uruchamia wszystkie przykłady po kolei.</li>
 * </ul>
 * Przykład {@code decorator} czyta linię ze standardowego wejścia (gdy jest puste,
 * używa tekstu domyślnego). W trybie {@code all} standardowe wejście nie jest czytane
 * wcale, więc uruchomienie nigdy się nie blokuje.
 */
public final class Module4Examples {

    public record Example(String key, String javaClass, Runnable run) {
    }

    private static final List<Example> EXAMPLES = List.of(
            new Example("abstract-factory", "creational.abstractfactory.Application",
                    () -> pl.training.module4.creational.abstractfactory.Application.main(new String[0])),
            new Example("builder", "creational.builder.Application",
                    () -> pl.training.module4.creational.builder.Application.main(new String[0])),
            new Example("factory-method", "creational.factorymethod.Application",
                    () -> pl.training.module4.creational.factorymethod.Application.main(new String[0])),
            new Example("prototype", "creational.prototype.Application",
                    Module4Examples::runPrototype),
            new Example("singleton", "creational.singleton.Application",
                    () -> pl.training.module4.creational.singleton.Application.main(new String[0])),
            new Example("adapter", "structural.adapter.Application",
                    () -> pl.training.module4.structural.adapter.Application.main(new String[0])),
            new Example("bridge", "structural.bridge.Application",
                    () -> pl.training.module4.structural.bridge.Application.main(new String[0])),
            new Example("composite", "structural.composite.Application",
                    () -> pl.training.module4.structural.composite.Application.main(new String[0])),
            new Example("decorator", "structural.decorator.Application (reads a line from stdin)",
                    () -> pl.training.module4.structural.decorator.Application.main(new String[0])),
            new Example("facade", "structural.facade.Application",
                    () -> pl.training.module4.structural.facade.Application.main(new String[0])),
            new Example("flyweight", "structural.flyweight.Application",
                    () -> pl.training.module4.structural.flyweight.Application.main(new String[0])),
            new Example("proxy", "structural.proxy.Application",
                    () -> pl.training.module4.structural.proxy.Application.main(new String[0])),
            new Example("chain-of-responsibility", "behavioral.chainofresponsibility.Application",
                    () -> pl.training.module4.behavioral.chainofresponsibility.Application.main(new String[0])),
            new Example("command", "behavioral.command.Application",
                    () -> pl.training.module4.behavioral.command.Application.main(new String[0])),
            new Example("interpreter", "behavioral.interpreter.Application",
                    () -> pl.training.module4.behavioral.interpreter.Application.main(new String[0])),
            new Example("iterator", "behavioral.iterator.Application",
                    () -> pl.training.module4.behavioral.iterator.Application.main(new String[0])),
            new Example("mediator", "behavioral.mediator.Application",
                    () -> pl.training.module4.behavioral.mediator.Application.main(new String[0])),
            new Example("memento", "behavioral.memento.Application",
                    () -> pl.training.module4.behavioral.memento.Application.main(new String[0])),
            new Example("observer", "behavioral.observer.Application",
                    () -> pl.training.module4.behavioral.observer.Application.main(new String[0])),
            new Example("state", "behavioral.state.Application",
                    () -> pl.training.module4.behavioral.state.Application.main(new String[0])),
            new Example("strategy", "behavioral.strategy.Application",
                    () -> pl.training.module4.behavioral.strategy.Application.main(new String[0])),
            new Example("template-method", "behavioral.templatemethod.Application",
                    () -> pl.training.module4.behavioral.templatemethod.Application.main(new String[0])),
            new Example("visitor", "behavioral.visitor.Application",
                    () -> pl.training.module4.behavioral.visitor.Application.main(new String[0])),
            new Example("fn-chain", "fn.FunctionalStyleChain",
                    () -> pl.training.module4.fn.FunctionalStyleChain.main(new String[0])),
            new Example("fn-command", "fn.FunctionalStyleCommand",
                    () -> pl.training.module4.fn.FunctionalStyleCommand.main(new String[0])),
            new Example("fn-decorator", "fn.FunctionalStyleDecorator",
                    () -> pl.training.module4.fn.FunctionalStyleDecorator.main(new String[0])),
            new Example("fn-factory", "fn.FunctionalStyleFactory",
                    () -> pl.training.module4.fn.FunctionalStyleFactory.main(new String[0])),
            new Example("fn-iterator", "fn.FunctionalStyleIterator",
                    () -> pl.training.module4.fn.FunctionalStyleIterator.main(new String[0])),
            new Example("fn-memento", "fn.FunctionalStyleMemento",
                    () -> pl.training.module4.fn.FunctionalStyleMemento.main(new String[0])),
            new Example("fn-observer", "fn.FunctionalStyleObserver",
                    () -> pl.training.module4.fn.FunctionalStyleObserver.main(new String[0])),
            new Example("fn-singleton", "fn.FunctionalStyleSingleton",
                    () -> pl.training.module4.fn.FunctionalStyleSingleton.main(new String[0])),
            new Example("fn-state", "fn.FunctionalStyleState",
                    () -> pl.training.module4.fn.FunctionalStyleState.main(new String[0])),
            new Example("fn-strategy", "fn.FunctionalStyleStrategy",
                    () -> pl.training.module4.fn.FunctionalStyleStrategy.main(new String[0])),
            new Example("fn-template", "fn.FunctionalStyleTemplate",
                    () -> pl.training.module4.fn.FunctionalStyleTemplate.main(new String[0])));

    private Module4Examples() {
    }

    public static List<Example> examples() {
        return EXAMPLES;
    }

    public static void main(String[] args) {
        int exitCode = execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Logika {@link #main} bez {@code System.exit} - zwraca kod wyjścia (0 albo 1).
     */
    public static int execute(String[] args) {
        if (args.length == 0) {
            printUsage(System.out);
            return 0;
        }
        String key = args[0].trim().toLowerCase(Locale.ROOT);
        if (key.equals("all")) {
            runAll();
            return 0;
        }
        if (!tryRun(key)) {
            System.err.println("Unknown example: " + args[0]);
            printUsage(System.err);
            return 1;
        }
        return 0;
    }

    public static boolean tryRun(String key) {
        Optional<Example> example = EXAMPLES.stream()
                .filter(candidate -> candidate.key().equals(key))
                .findFirst();
        example.ifPresent(found -> found.run().run());
        return example.isPresent();
    }

    public static void runAll() {
        InputStream originalIn = System.in;
        // przy uruchamianiu wszystkiego nigdy nie czekamy na wejście (konsola ani otwarty potok)
        // - dekorator użyje tekstu domyślnego
        System.setIn(new ByteArrayInputStream(new byte[0]));
        try {
            for (var example : EXAMPLES) {
                System.out.flush();
                System.err.flush();
                System.out.println("=== " + example.key() + " ===");
                example.run().run();
            }
        } finally {
            System.setIn(originalIn);
        }
    }

    private static void printUsage(PrintStream stream) {
        stream.println("Usage: java pl.training.module4.Module4Examples <example|all>");
        stream.println("Available examples (Java class in pl.training.module4):");
        for (var example : EXAMPLES) {
            stream.printf("  %-24s %s%n", example.key(), example.javaClass());
        }
        stream.printf("  %-24s %s%n", "all", "runs every example in the order above");
    }

    private static void runPrototype() {
        try {
            pl.training.module4.creational.prototype.Application.main(new String[0]);
        } catch (CloneNotSupportedException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
