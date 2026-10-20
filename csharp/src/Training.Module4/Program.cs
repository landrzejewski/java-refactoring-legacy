namespace Training.Module4;

/// <summary>
/// Single entry point replacing the many Java <c>main</c> methods.
/// <list type="bullet">
/// <item><c>dotnet run --project src/Training.Module4</c> - lists available example keys</item>
/// <item><c>dotnet run --project src/Training.Module4 -- builder</c> - runs one example</item>
/// <item><c>dotnet run --project src/Training.Module4 -- all</c> - runs every example in order</item>
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
        new("abstract-factory", "creational.abstractfactory.Application", global::Training.Module4.Creational.AbstractFactory.Application.Run),
        new("builder", "creational.builder.Application", global::Training.Module4.Creational.Builder.Application.Run),
        new("factory-method", "creational.factorymethod.Application", global::Training.Module4.Creational.FactoryMethod.Application.Run),
        new("prototype", "creational.prototype.Application", global::Training.Module4.Creational.Prototype.Application.Run),
        new("singleton", "creational.singleton.Application", global::Training.Module4.Creational.Singleton.Application.Run),
        new("adapter", "structural.adapter.Application", global::Training.Module4.Structural.Adapter.Application.Run),
        new("bridge", "structural.bridge.Application", global::Training.Module4.Structural.Bridge.Application.Run),
        new("composite", "structural.composite.Application", global::Training.Module4.Structural.Composite.Application.Run),
        new("decorator", "structural.decorator.Application (reads a line from stdin)", global::Training.Module4.Structural.Decorator.Application.Run),
        new("facade", "structural.facade.Application", global::Training.Module4.Structural.Facade.Application.Run),
        new("flyweight", "structural.flyweight.Application", global::Training.Module4.Structural.Flyweight.Application.Run),
        new("proxy", "structural.proxy.Application", global::Training.Module4.Structural.Proxy.Application.Run),
        new("chain-of-responsibility", "behavioral.chainofresponsibility.Application", global::Training.Module4.Behavioral.ChainOfResponsibility.Application.Run),
        new("command", "behavioral.command.Application", global::Training.Module4.Behavioral.Command.Application.Run),
        new("interpreter", "behavioral.interpreter.Application", global::Training.Module4.Behavioral.Interpreter.Application.Run),
        new("iterator", "behavioral.iterator.Application", global::Training.Module4.Behavioral.Iterator.Application.Run),
        new("mediator", "behavioral.mediator.Application", global::Training.Module4.Behavioral.Mediator.Application.Run),
        new("memento", "behavioral.memento.Application", global::Training.Module4.Behavioral.Memento.Application.Run),
        new("observer", "behavioral.observer.Application", global::Training.Module4.Behavioral.Observer.Application.Run),
        new("state", "behavioral.state.Application", global::Training.Module4.Behavioral.State.Application.Run),
        new("strategy", "behavioral.strategy.Application", global::Training.Module4.Behavioral.Strategy.Application.Run),
        new("template-method", "behavioral.templatemethod.Application", global::Training.Module4.Behavioral.TemplateMethod.Application.Run),
        new("visitor", "behavioral.visitor.Application", global::Training.Module4.Behavioral.Visitor.Application.Run),
        new("fn-chain", "fn.FunctionalStyleChain", global::Training.Module4.Fn.FunctionalStyleChain.Run),
        new("fn-command", "fn.FunctionalStyleCommand", global::Training.Module4.Fn.FunctionalStyleCommand.Run),
        new("fn-decorator", "fn.FunctionalStyleDecorator", global::Training.Module4.Fn.FunctionalStyleDecorator.Run),
        new("fn-factory", "fn.FunctionalStyleFactory", global::Training.Module4.Fn.FunctionalStyleFactory.Run),
        new("fn-iterator", "fn.FunctionalStyleIterator", global::Training.Module4.Fn.FunctionalStyleIterator.Run),
        new("fn-memento", "fn.FunctionalStyleMemento", global::Training.Module4.Fn.FunctionalStyleMemento.Run),
        new("fn-observer", "fn.FunctionalStyleObserver", global::Training.Module4.Fn.FunctionalStyleObserver.Run),
        new("fn-singleton", "fn.FunctionalStyleSingleton", global::Training.Module4.Fn.FunctionalStyleSingleton.Run),
        new("fn-state", "fn.FunctionalStyleState", global::Training.Module4.Fn.FunctionalStyleState.Run),
        new("fn-strategy", "fn.FunctionalStyleStrategy", global::Training.Module4.Fn.FunctionalStyleStrategy.Run),
        new("fn-template", "fn.FunctionalStyleTemplate", global::Training.Module4.Fn.FunctionalStyleTemplate.Run),
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
        writer.WriteLine("Usage: dotnet run --project src/Training.Module4 -- <example|all>");
        writer.WriteLine("Available examples (Java class in pl.training.module4):");
        foreach (var example in Examples)
        {
            writer.WriteLine($"  {example.Key,-24} {example.JavaClass}");
        }
        writer.WriteLine($"  {"all",-24} runs every example in the order above");
    }
}
