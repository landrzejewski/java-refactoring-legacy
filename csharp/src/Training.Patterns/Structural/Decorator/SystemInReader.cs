namespace Training.Patterns.Structural.Decorator;

/// <summary>
/// Java: <c>new Scanner(System.in).nextLine()</c>. Reads one line from <see cref="Console.In"/>.
/// Deviation: when stdin is empty/closed (Java would throw <c>NoSuchElementException</c>) the
/// <see cref="DefaultText"/> is returned, so the example also runs non-interactively (<c>-- all</c>, tests, CI).
/// </summary>
public class SystemInReader : IReader
{
    public const string DefaultText = "Hello Decorator Pattern";

    public string GetText() => Console.In.ReadLine() ?? DefaultText;
}
