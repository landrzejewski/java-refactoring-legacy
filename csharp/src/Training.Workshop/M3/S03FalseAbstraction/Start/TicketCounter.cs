namespace Training.Workshop.M3.S03FalseAbstraction.Start;

/// <summary>Sprzedaż pojedynczego biletu - "quantity" i "pass" nic tu nie znaczą.</summary>
public sealed class TicketCounter
{
    private readonly Pricing _pricing = new();

    public decimal Ticket(string format, bool morning, bool ownGlasses)
    {
        return _pricing.Price(format, 1, false, morning, ownGlasses);
    }
}
