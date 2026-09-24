namespace Training.Workshop.M6.S15Command.Step2;

/// <summary>Krok 2: gałąź REPORT jako obiekt komendy.</summary>
public sealed class ReportCommand : IConsoleCommand
{
    public string Execute(string args, Till till)
    {
        return "Kasa: " + till.Cash + ", biletow: " + till.Tickets;
    }
}
