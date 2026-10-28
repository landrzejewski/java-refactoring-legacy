package pl.training.examples;

import java.util.List;

import pl.training.examples.fn.FunctionalStyleChain;
import pl.training.examples.fn.FunctionalStyleCommand;
import pl.training.examples.fn.FunctionalStyleDecorator;
import pl.training.examples.fn.FunctionalStyleFactory;
import pl.training.examples.fn.FunctionalStyleIterator;
import pl.training.examples.fn.FunctionalStyleMemento;
import pl.training.examples.fn.FunctionalStyleObserver;
import pl.training.examples.fn.FunctionalStyleSingleton;
import pl.training.examples.fn.FunctionalStyleState;
import pl.training.examples.fn.FunctionalStyleStrategy;
import pl.training.examples.fn.FunctionalStyleTemplate;

/**
 * Rejestr przykładów wzorców GoF (moduł 4). Pozwala uruchomić pojedynczy
 * przykład po kluczu, wszystkie po kolei ({@code all}) albo wypisać listę
 * dostępnych kluczy (brak argumentów):
 *
 * <pre>
 * java -cp target/classes pl.training.examples.Module4Examples
 * java -cp target/classes pl.training.examples.Module4Examples decorator
 * java -cp target/classes pl.training.examples.Module4Examples all
 * </pre>
 */
public final class Module4Examples {

    public record Example(String key, Runnable runner) {

        public void run() {
            runner.run();
        }

    }

    private static final String[] NO_ARGS = new String[0];

    private static final List<Example> EXAMPLES = List.of(
            new Example("singleton", () -> pl.training.examples.creational.singleton.Application.main(NO_ARGS)),
            new Example("builder", () -> pl.training.examples.creational.builder.Application.main(NO_ARGS)),
            new Example("prototype", Module4Examples::runPrototype),
            new Example("factory-method", () -> pl.training.examples.creational.factorymethod.Application.main(NO_ARGS)),
            new Example("abstract-factory", () -> pl.training.examples.creational.abstractfactory.Application.main(NO_ARGS)),
            new Example("adapter", () -> pl.training.examples.structural.adapter.Application.main(NO_ARGS)),
            new Example("bridge", () -> pl.training.examples.structural.bridge.Application.main(NO_ARGS)),
            new Example("composite", () -> pl.training.examples.structural.composite.Application.main(NO_ARGS)),
            new Example("decorator", () -> pl.training.examples.structural.decorator.Application.main(NO_ARGS)),
            new Example("facade", () -> pl.training.examples.structural.facade.Application.main(NO_ARGS)),
            new Example("flyweight", () -> pl.training.examples.structural.flyweight.Application.main(NO_ARGS)),
            new Example("proxy", () -> pl.training.examples.structural.proxy.Application.main(NO_ARGS)),
            new Example("chain-of-responsibility", () -> pl.training.examples.behavioral.chainofresponsibility.Application.main(NO_ARGS)),
            new Example("command", () -> pl.training.examples.behavioral.command.Application.main(NO_ARGS)),
            new Example("interpreter", () -> pl.training.examples.behavioral.interpreter.Application.main(NO_ARGS)),
            new Example("iterator", () -> pl.training.examples.behavioral.iterator.Application.main(NO_ARGS)),
            new Example("mediator", () -> pl.training.examples.behavioral.mediator.Application.main(NO_ARGS)),
            new Example("memento", () -> pl.training.examples.behavioral.memento.Application.main(NO_ARGS)),
            new Example("observer", () -> pl.training.examples.behavioral.observer.Application.main(NO_ARGS)),
            new Example("state", () -> pl.training.examples.behavioral.state.Application.main(NO_ARGS)),
            new Example("strategy", () -> pl.training.examples.behavioral.strategy.Application.main(NO_ARGS)),
            new Example("template-method", () -> pl.training.examples.behavioral.templatemethod.Application.main(NO_ARGS)),
            new Example("visitor", () -> pl.training.examples.behavioral.visitor.Application.main(NO_ARGS)),
            new Example("fn-chain", () -> FunctionalStyleChain.main(NO_ARGS)),
            new Example("fn-command", () -> FunctionalStyleCommand.main(NO_ARGS)),
            new Example("fn-decorator", () -> FunctionalStyleDecorator.main(NO_ARGS)),
            new Example("fn-factory", () -> FunctionalStyleFactory.main(NO_ARGS)),
            new Example("fn-iterator", () -> FunctionalStyleIterator.main(NO_ARGS)),
            new Example("fn-memento", () -> FunctionalStyleMemento.main(NO_ARGS)),
            new Example("fn-observer", () -> FunctionalStyleObserver.main(NO_ARGS)),
            new Example("fn-singleton", () -> FunctionalStyleSingleton.main(NO_ARGS)),
            new Example("fn-state", () -> FunctionalStyleState.main(NO_ARGS)),
            new Example("fn-strategy", () -> FunctionalStyleStrategy.main(NO_ARGS)),
            new Example("fn-template", () -> FunctionalStyleTemplate.main(NO_ARGS)));

    private Module4Examples() {
    }

    public static List<Example> examples() {
        return EXAMPLES;
    }

    public static boolean tryRun(String key) {
        for (Example example : EXAMPLES) {
            if (example.key().equals(key)) {
                example.run();
                return true;
            }
        }
        return false;
    }

    public static int execute(String[] args) {
        if (args.length == 0) {
            printUsage();
            return 0;
        }
        String key = args[0];
        if ("all".equals(key)) {
            for (Example example : EXAMPLES) {
                System.out.println("=== " + example.key() + " ===");
                example.run();
            }
            return 0;
        }
        if (tryRun(key)) {
            return 0;
        }
        System.out.println("Unknown example: " + key);
        printUsage();
        return 1;
    }

    public static void main(String[] args) {
        System.exit(execute(args));
    }

    private static void runPrototype() {
        try {
            pl.training.examples.creational.prototype.Application.main(NO_ARGS);
        } catch (CloneNotSupportedException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java pl.training.examples.Module4Examples <key>|all");
        System.out.println("Available examples:");
        for (Example example : EXAMPLES) {
            System.out.println("  " + example.key());
        }
    }

}
