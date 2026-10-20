using System.Globalization;
using System.Runtime.CompilerServices;

namespace Training.Module4;

/// <summary>
/// Minimal stand-in for <c>java.util.logging.Logger</c> with the default <c>ConsoleHandler</c>/<c>SimpleFormatter</c>:
/// writes two lines to standard error, e.g.
/// <code>
/// Sep 18, 2026 10:30:36 PM Training.Module4.Creational.Builder.Director Run
/// INFO: Connection url: ...
/// </code>
/// </summary>
public sealed class JulLogger
{
    private static readonly Lock OutputLock = new();
    private readonly string name;

    private JulLogger(string name)
    {
        this.name = name;
    }

    public static JulLogger GetLogger(Type type) => new(type.FullName ?? type.Name);

    public void Info(string? message, [CallerMemberName] string sourceMethod = "")
    {
        var timestamp = DateTime.Now.ToString("MMM dd, yyyy h:mm:ss tt", CultureInfo.InvariantCulture);
        lock (OutputLock)
        {
            Console.Error.WriteLine($"{timestamp} {name} {sourceMethod}");
            Console.Error.WriteLine($"INFO: {message ?? "null"}");
        }
    }
}
