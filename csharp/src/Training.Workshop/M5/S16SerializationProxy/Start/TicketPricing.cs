using Training.Workshop.Shared;

namespace Training.Workshop.M5.S16SerializationProxy.Start;

/// <summary>
/// Start: serwis sealed bez interfejsu. DispatchProxy umie opakować tylko interfejsy, a proxy klasowe
/// (Castle DynamicProxy - interceptory kontenerów DI, lazy loading NHibernate/EF Core proxies) robi PODKLASĘ
/// i przechwytuje tylko metody wirtualne, więc nie ruszy klasy sealed ani metod niewirtualnych.
/// Transakcje, audyt czy cache "z atrybutu" po cichu nie zadziałają.
/// </summary>
public sealed class TicketPricing
{
    public Money StudentPrice(Money basePrice)
    {
        return basePrice.Minus(basePrice.Percent(25));
    }
}
