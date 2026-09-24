namespace Training.Workshop.M3.S16TemporalCoupling.Start;

/// <summary>
/// Klient drukarki: kasa wydająca bilety. Musi znać protokół drukarki
/// (trzy wywołania przed Print). Test woła tylko <see cref="Issue"/>.
/// </summary>
public sealed class TicketDesk
{
    private readonly TicketPrinter _printer = new();

    public string Issue(Screening screening, int seat, string buyer)
    {
        _printer.SelectScreening(screening);
        _printer.SelectSeat(seat);
        _printer.ForBuyer(buyer);
        return _printer.Print();
    }
}
