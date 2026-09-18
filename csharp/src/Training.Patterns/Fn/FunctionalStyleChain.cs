namespace Training.Patterns.Fn;

public class FunctionalStyleChain
{
    public static void Run()
    {
        Func<string, string> step1 = s => s + " -> validate";
        Func<string, string> step2 = s => s + " -> authenticate";
        Func<string, string> step3 = s => s + " -> log";
        var chain = step1.AndThen(step2).AndThen(step3);
        Console.WriteLine(chain("Request"));
    }
}
