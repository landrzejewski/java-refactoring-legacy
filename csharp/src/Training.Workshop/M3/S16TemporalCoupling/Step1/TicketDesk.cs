namespace Training.Workshop.M3.S16TemporalCoupling.Step1;

/// <summary>Krok 1: kasa po Change Signature - jedno wywołanie, bez protokołu.</summary>
public sealed class TicketDesk
{
    private readonly TicketPrinter _printer = new();

    public string Issue(Screening screening, int seat, string buyer)
    {
        return _printer.Print(screening, seat, buyer);
    }
}
