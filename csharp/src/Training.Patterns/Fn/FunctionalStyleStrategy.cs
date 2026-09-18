namespace Training.Patterns.Fn;

public class FunctionalStyleStrategy
{
    public static void Run()
    {
        Func<string, string> upperCaseStrategy = s => s.ToUpperInvariant();
        Func<string, string> lowerCaseStrategy = s => s.ToLowerInvariant();
        Func<string, string> reverseStrategy = s => new string(s.Reverse().ToArray());
        var input = "Functional Patterns";
        ExecuteStrategy("UPPER", input, upperCaseStrategy);
        ExecuteStrategy("LOWER", input, lowerCaseStrategy);
        ExecuteStrategy("REVERSE", input, reverseStrategy);
    }

    internal static void ExecuteStrategy(string name, string input, Func<string, string> strategy)
    {
        Console.WriteLine(name + ": " + strategy(input));
    }
}
