namespace Training.Patterns.Fn;

public class FunctionalStyleCommand
{
    public static void Run()
    {
        Action open = () => Console.WriteLine("Opening file...");
        Action save = () => Console.WriteLine("Saving file...");
        Action close = () => Console.WriteLine("Closing file...");
        Execute(open);
        Execute(save);
        Execute(close);
    }

    internal static void Execute(Action command)
    {
        command();
    }
}
