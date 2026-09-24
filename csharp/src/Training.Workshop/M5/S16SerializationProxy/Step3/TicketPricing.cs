using Training.Workshop.Shared;

namespace Training.Workshop.M5.S16SerializationProxy.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): implementacja może zostać sealed - DispatchProxy opakowuje interfejs IPricing.
/// Uwaga: wywołanie this.InnaMetoda() wewnątrz klasy i tak omija proxy (self-invocation).
/// </summary>
public sealed class TicketPricing : IPricing
{
    public Money StudentPrice(Money basePrice)
    {
        return basePrice.Minus(basePrice.Percent(25));
    }
}
