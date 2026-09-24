namespace Training.Workshop.M3.S16TemporalCoupling.Step2;

/// <summary>
/// Krok 2: Introduce Parameter Object - dane biletu jako jedna wartość,
/// kompletna od chwili utworzenia (null odrzucony od razu, a nie w Print).
/// </summary>
public sealed record TicketRequest
{
    public TicketRequest(Screening screening, int seat, string buyer)
    {
        ArgumentNullException.ThrowIfNull(screening);
        ArgumentNullException.ThrowIfNull(buyer);
        Screening = screening;
        Seat = seat;
        Buyer = buyer;
    }

    public Screening Screening { get; }

    public int Seat { get; }

    public string Buyer { get; }
}
