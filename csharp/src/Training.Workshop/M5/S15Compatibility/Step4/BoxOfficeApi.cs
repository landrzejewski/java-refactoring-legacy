using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): stara sygnatura wraca jako przestarzałe przeciążenie delegujące.
/// Stare assembly znajdują swoją sygnaturę, nowi klienci widzą ostrzeżenie [Obsolete].
/// Usunięcie planujemy jako osobną, zapowiedzianą zmianę łamiącą (np. w wersji 3.0).
/// </summary>
public sealed class BoxOfficeApi
{
    public Money Quote(Ticket ticket)
    {
        return ticket.Price();
    }

    /// <summary>Zgodność binarna ze skompilowanymi wtyczkami 1.x.</summary>
    [Obsolete("od 2.0: użyj Quote(Ticket)")]
    public Money Quote(StudentTicket ticket)
    {
        return Quote((Ticket)ticket);
    }
}
