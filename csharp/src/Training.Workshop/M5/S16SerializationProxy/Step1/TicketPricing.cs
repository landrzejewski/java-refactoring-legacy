using Training.Workshop.Shared;

namespace Training.Workshop.M5.S16SerializationProxy.Step1;

/// <summary>Krok 1: bez zmian - nadal sealed i bez interfejsu (tym zajmiemy się w kroku 3).</summary>
public sealed class TicketPricing
{
    public Money StudentPrice(Money basePrice)
    {
        return basePrice.Minus(basePrice.Percent(25));
    }
}
