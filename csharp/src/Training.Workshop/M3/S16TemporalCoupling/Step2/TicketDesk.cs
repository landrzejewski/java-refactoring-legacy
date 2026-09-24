namespace Training.Workshop.M3.S16TemporalCoupling.Step2;

/// <summary>Krok 2: kasa buduje kompletne żądanie biletu.</summary>
public sealed class TicketDesk
{
    private readonly TicketPrinter _printer = new();

    public string Issue(Screening screening, int seat, string buyer)
    {
        return _printer.Print(new TicketRequest(screening, seat, buyer));
    }
}
