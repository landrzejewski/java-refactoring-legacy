using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step1;

/// <summary>Krok 1: klient bez zmian.</summary>
public sealed class PriceBoard
{
    private readonly TicketPricer _pricer = new();

    public Money PriceFor(PriceRequest request)
    {
        return _pricer.Price(request.Base, request.TicketType, request.Program);
    }
}
