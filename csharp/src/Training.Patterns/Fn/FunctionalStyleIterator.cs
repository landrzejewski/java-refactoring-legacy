namespace Training.Patterns.Fn;

public class FunctionalStyleIterator
{
    public static void Run()
    {
        foreach (var text in new[] { "one", "two", "three" }
                     .Select(s => s.ToUpperInvariant()))
        {
            Console.WriteLine(text);
        }
    }
}
