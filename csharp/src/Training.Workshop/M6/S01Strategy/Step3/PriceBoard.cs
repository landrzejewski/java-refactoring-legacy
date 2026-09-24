using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step3;

/// <summary>Krok 3: klient składa kontekst ze strategią wybraną w DiscountPrograms (Change Signature).</summary>
public sealed class PriceBoard
{
    public Money PriceFor(PriceRequest request)
    {
        return new TicketPricer(DiscountPrograms.ForName(request.Program))
            .Price(request.Base, request.TicketType);
    }
}
