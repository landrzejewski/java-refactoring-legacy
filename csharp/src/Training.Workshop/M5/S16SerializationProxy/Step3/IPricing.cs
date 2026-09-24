using Training.Workshop.Shared;

namespace Training.Workshop.M5.S16SerializationProxy.Step3;

/// <summary>
/// Krok 3: Extract Interface dla integracji - rola, którą kontener DI może opakować dynamicznym proxy
/// (DispatchProxy) bez dziedziczenia po implementacji.
/// </summary>
public interface IPricing
{
    Money StudentPrice(Money basePrice);
}
