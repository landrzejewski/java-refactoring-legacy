namespace Training.Patterns.Fn;

public class FunctionalStyleTemplate
{
    internal class Processor
    {
        internal void Process(Action<string> step)
        {
            Console.WriteLine("Start processing...");
            step("data");
            Console.WriteLine("Finish processing!");
        }
    }

    public static void Run()
    {
        var p = new Processor();
        // hook behavior supplied by lambda
        p.Process(d => Console.WriteLine("Custom step with: " + d));
    }
}
