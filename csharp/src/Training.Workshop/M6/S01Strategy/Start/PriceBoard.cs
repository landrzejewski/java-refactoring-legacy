using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Start;

/// <summary>Start: klient cennika - kasa wycenia bilet wg programu z konfiguracji.</summary>
public sealed class PriceBoard
{
    private readonly TicketPricer _pricer = new();

    public Money PriceFor(PriceRequest request)
    {
        return _pricer.Price(request.Base, request.TicketType, request.Program);
    }
}
