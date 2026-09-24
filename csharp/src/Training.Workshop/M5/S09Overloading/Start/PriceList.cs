using Training.Workshop.Shared;

namespace Training.Workshop.M5.S09Overloading.Start;

/// <summary>
/// Start: pułapka 1 - przeciążenia wybiera KOMPILATOR według typu deklarowanego argumentu.
/// Dopóki klient miał List&lt;StudentTicket&gt;, działało. Po przejściu na IReadOnlyList&lt;Ticket&gt;
/// ten sam tekst <c>Price(ticket)</c> wybiera Price(Ticket) - student płaci pełną cenę.
/// </summary>
public sealed class PriceList
{
    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice;
    }

    public Money Price(StudentTicket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(25));
    }
}
