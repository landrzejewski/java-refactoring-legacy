namespace Training.Patterns;

/// <summary>
/// Single entry point replacing the many Java <c>main</c> methods.
/// <list type="bullet">
/// <item><c>dotnet run --project src/Training.Patterns</c> - lists available example keys</item>
/// <item><c>dotnet run --project src/Training.Patterns -- builder</c> - runs one example</item>
/// <item><c>dotnet run --project src/Training.Patterns -- all</c> - runs every example in order</item>
/// </list>
/// The <c>decorator</c> example reads one line from standard input (like Java's <c>Scanner</c>).
/// When stdin is empty/closed it falls back to a default text; in <c>all</c> mode an interactive
/// console is never read, so the run does not block.
/// </summary>
public static class Program
{
    public sealed record Example(string Key, string JavaClass, Action Run);

    public static IReadOnlyList<Example> Examples { get; } =
    [
        new("abstract-factory", "creational.abstractfactory.Application", global::Training.Patterns.Creational.AbstractFactory.Application.Run),
        new("builder", "creational.builder.Application", global::Training.Patterns.Creational.Builder.Application.Run),
        new("factory-method", "creational.factorymethod.Application", global::Training.Patterns.Creational.FactoryMethod.Application.Run),
        new("prototype", "creational.prototype.Application", global::Training.Patterns.Creational.Prototype.Application.Run),
        new("singleton", "creational.singleton.Application", global::Training.Patterns.Creational.Singleton.Application.Run),
        new("adapter", "structural.adapter.Application", global::Training.Patterns.Structural.Adapter.Application.Run),
        new("composite", "structural.composite.Application", global::Training.Patterns.Structural.Composite.Application.Run),
        new("decorator", "structural.decorator.Application (reads a line from stdin)", global::Training.Patterns.Structural.Decorator.Application.Run),
        new("facade", "structural.facade.Application", global::Training.Patterns.Structural.Facade.Application.Run),
        new("flyweight", "structural.flyweight.Application", global::Training.Patterns.Structural.Flyweight.Application.Run),
        new("proxy", "structural.proxy.Application", global::Training.Patterns.Structural.Proxy.Application.Run),
        new("chain-of-responsibility", "behavioral.chainofresponsibility.Application", global::Training.Patterns.Behavioral.ChainOfResponsibility.Application.Run),
        new("command", "behavioral.command.Application", global::Training.Patterns.Behavioral.Command.Application.Run),
        new("interpreter", "behavioral.interpreter.Application", global::Training.Patterns.Behavioral.Interpreter.Application.Run),
        new("iterator", "behavioral.iterator.Application", global::Training.Patterns.Behavioral.Iterator.Application.Run),
        new("memento", "behavioral.memento.Application", global::Training.Patterns.Behavioral.Memento.Application.Run),
        new("observer", "behavioral.observer.Application", global::Training.Patterns.Behavioral.Observer.Application.Run),
        new("state", "behavioral.state.Application", global::Training.Patterns.Behavioral.State.Application.Run),
        new("strategy", "behavioral.strategy.Application", global::Training.Patterns.Behavioral.Strategy.Application.Run),
        new("template-method", "behavioral.templatemethod.Application", global::Training.Patterns.Behavioral.TemplateMethod.Application.Run),
        new("visitor", "behavioral.visitor.Application", global::Training.Patterns.Behavioral.Visitor.Application.Run),
        new("fn-chain", "fn.FunctionalStyleChain", global::Training.Patterns.Fn.FunctionalStyleChain.Run),
        new("fn-command", "fn.FunctionalStyleCommand", global::Training.Patterns.Fn.FunctionalStyleCommand.Run),
        new("fn-decorator", "fn.FunctionalStyleDecorator", global::Training.Patterns.Fn.FunctionalStyleDecorator.Run),
        new("fn-factory", "fn.FunctionalStyleFactory", global::Training.Patterns.Fn.FunctionalStyleFactory.Run),
        new("fn-iterator", "fn.FunctionalStyleIterator", global::Training.Patterns.Fn.FunctionalStyleIterator.Run),
        new("fn-memento", "fn.FunctionalStyleMemento", global::Training.Patterns.Fn.FunctionalStyleMemento.Run),
        new("fn-observer", "fn.FunctionalStyleObserver", global::Training.Patterns.Fn.FunctionalStyleObserver.Run),
        new("fn-singleton", "fn.FunctionalStyleSingleton", global::Training.Patterns.Fn.FunctionalStyleSingleton.Run),
        new("fn-state", "fn.FunctionalStyleState", global::Training.Patterns.Fn.FunctionalStyleState.Run),
        new("fn-strategy", "fn.FunctionalStyleStrategy", global::Training.Patterns.Fn.FunctionalStyleStrategy.Run),
        new("fn-template", "fn.FunctionalStyleTemplate", global::Training.Patterns.Fn.FunctionalStyleTemplate.Run),
    ];

    public static int Main(string[] args)
    {
        if (!Console.IsOutputRedirected && Console.OutputEncoding.CodePage != System.Text.Encoding.UTF8.CodePage)
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8; // fn-state prints "→"
        }
        if (args.Length == 0)
        {
            PrintUsage(Console.Out);
            return 0;
        }
        var key = args[0].Trim().ToLowerInvariant();
        if (key == "all")
        {
            RunAll();
            return 0;
        }
        if (!TryRun(key))
        {
            Console.Error.WriteLine($"Unknown example: {args[0]}");
            PrintUsage(Console.Error);
            return 1;
        }
        return 0;
    }

    public static bool TryRun(string key)
    {
        var example = Examples.FirstOrDefault(candidate => candidate.Key == key);
        if (example is null)
        {
            return false;
        }
        example.Run();
        return true;
    }

    public static void RunAll()
    {
        if (!Console.IsInputRedirected)
        {
            // never wait for keyboard input when running everything - decorator uses its default text
            Console.SetIn(TextReader.Null);
        }
        foreach (var example in Examples)
        {
            Console.Out.Flush();
            Console.Error.Flush();
            Console.WriteLine($"=== {example.Key} ===");
            example.Run();
        }
    }

    private static void PrintUsage(TextWriter writer)
    {
        writer.WriteLine("Usage: dotnet run --project src/Training.Patterns -- <example|all>");
        writer.WriteLine("Available examples (Java class in pl.training.patterns):");
        foreach (var example in Examples)
        {
            writer.WriteLine($"  {example.Key,-24} {example.JavaClass}");
        }
        writer.WriteLine($"  {"all",-24} runs every example in the order above");
    }
}
