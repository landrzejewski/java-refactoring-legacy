namespace Training.Workshop.M6.S15Command.Step2;

/// <summary>
/// Krok 2: Extract Class dla każdej gałęzi - komendy jako obiekty, stan w Till.
/// Dyspozytor warunkowy jeszcze zostaje; zmieniamy jedną rzecz naraz.
/// </summary>
public sealed class CashierConsole
{
    private readonly Till _till = new();
    private readonly IConsoleCommand _sell = new SellCommand();
    private readonly IConsoleCommand _refund = new RefundCommand();
    private readonly IConsoleCommand _report = new ReportCommand();

    public string Handle(string line)
    {
        var parts = line.Trim().Split(' ', 2);
        var command = parts[0].ToUpperInvariant();
        var args = parts.Length > 1 ? parts[1] : "";
        if (command == "SELL")
        {
            return _sell.Execute(args, _till);
        }
        else if (command == "REFUND")
        {
            return _refund.Execute(args, _till);
        }
        else if (command == "REPORT")
        {
            return _report.Execute(args, _till);
        }
        return "Nieznana komenda: " + parts[0];
    }
}
