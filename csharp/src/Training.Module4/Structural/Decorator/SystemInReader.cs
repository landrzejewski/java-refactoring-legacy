namespace Training.Module4.Structural.Decorator;

/// <summary>
/// Czyta jedną linię ze standardowego wejścia. Gdy wejście jest puste lub
/// zamknięte, zwraca <see cref="DefaultText"/>, dzięki czemu przykład działa też
/// nieinteraktywnie (tryb <c>all</c>, testy, CI).
/// </summary>
public class SystemInReader : IReader
{
    public const string DefaultText = "Hello Decorator Pattern";

    public string GetText() => Console.In.ReadLine() ?? DefaultText;
}
