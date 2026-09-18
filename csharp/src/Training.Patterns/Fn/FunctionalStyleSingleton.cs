namespace Training.Patterns.Fn;

public class FunctionalStyleSingleton
{
    public static void Run()
    {
        var connection = Memoize(() => new DatabaseConnection());
        var c1 = connection();
        var c2 = connection();
        Console.WriteLine(JavaText.Of(ReferenceEquals(c1, c2))); // true
    }

    internal class DatabaseConnection
    {
        internal DatabaseConnection() { Console.WriteLine("Connecting to DB..."); }
    }

    // Java: anonymous Supplier with a synchronized lazy field -> Lazy<T> (thread-safe by default)
    internal static Func<T> Memoize<T>(Func<T> supplier)
    {
        var instance = new Lazy<T>(supplier, LazyThreadSafetyMode.ExecutionAndPublication);
        return () => instance.Value;
    }
}
