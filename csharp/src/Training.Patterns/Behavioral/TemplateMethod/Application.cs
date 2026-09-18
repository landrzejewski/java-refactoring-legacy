namespace Training.Patterns.Behavioral.TemplateMethod;

public class Application
{
    internal class UpperCaseProcessor : Processor<IReadOnlyList<string>, string>
    {
        protected override IReadOnlyList<string> Read() => ["template", "method"];

        protected override string Process(IReadOnlyList<string> data) => string.Join(" ", data).ToUpperInvariant();

        protected override void Write(string data)
        {
            Console.WriteLine(data);
        }
    }

    public static void Run()
    {
        new UpperCaseProcessor().Run();
    }
}
