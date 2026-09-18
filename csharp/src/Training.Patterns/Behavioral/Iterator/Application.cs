namespace Training.Patterns.Behavioral.Iterator;

public class Application
{
    public static void Run()
    {
        IReadOnlyList<int> collection = [1, 2, 3, 4, 5];
        using var iterator = collection.GetEnumerator();
        //------------------------------------------------------
        while (iterator.MoveNext())
        {
            Console.WriteLine(iterator.Current);
        }
    }
}
