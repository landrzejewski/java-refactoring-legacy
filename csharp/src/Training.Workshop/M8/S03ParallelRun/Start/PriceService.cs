using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Start;

/// <summary>
/// Start: usługa korzysta tylko ze starego kalkulatora. Kandydat leży obok nieużywany -
/// jedyne opcje to "włączyć i zobaczyć" albo wieczne testy ręczne.
/// </summary>
public sealed class PriceService
{
    private readonly LegacyPriceCalculator _legacy = new();

    public Money Price(TicketQuery query)
    {
        return _legacy.Price(query);
    }
}
