namespace Training.Patterns.Fn;

public class FunctionalStyleDecorator
{
    public static void Run()
    {
        Func<string, string> @base = s => s;
        Func<string, string> trim = s => s.Trim();
        Func<string, string> upper = s => s.ToUpperInvariant();
        Func<string, string> addBrackets = s => "[" + s + "]";
        var decorated =
                @base.AndThen(trim)
                    .AndThen(upper)
                    .AndThen(addBrackets);
        Console.WriteLine(decorated("   decorator pattern   "));
    }
}
