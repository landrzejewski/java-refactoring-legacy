namespace Training.Workshop.M6.S15Command.Step3;

/// <summary>
/// Krok 3: Replace Conditional Dispatcher with Command - rejestr komend zamiast if.
/// Równoważne, bo klucze są rozłączne; normalizacja klucza (ToUpperInvariant) zachowana.
/// </summary>
public sealed class CashierConsole
{
    private readonly Till _till = new();
    private readonly IReadOnlyDictionary<string, IConsoleCommand> _commands;

    public CashierConsole()
        : this(new Dictionary<string, IConsoleCommand>
        {
            ["SELL"] = new SellCommand(),
            ["REFUND"] = new RefundCommand(),
            ["REPORT"] = new ReportCommand(),
        })
    {
    }

    public CashierConsole(IReadOnlyDictionary<string, IConsoleCommand> commands)
    {
        _commands = new Dictionary<string, IConsoleCommand>(commands);
    }

    public string Handle(string line)
    {
        var parts = line.Trim().Split(' ', 2);
        if (!_commands.TryGetValue(parts[0].ToUpperInvariant(), out var command))
        {
            return "Nieznana komenda: " + parts[0];
        }
        return command.Execute(parts.Length > 1 ? parts[1] : "", _till);
    }
}
