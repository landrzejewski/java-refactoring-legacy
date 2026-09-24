using Training.Workshop.Shared;

namespace Training.Workshop.M5.S16SerializationProxy.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public sealed class TicketPricing
{
    public Money StudentPrice(Money basePrice)
    {
        return basePrice.Minus(basePrice.Percent(25));
    }
}
