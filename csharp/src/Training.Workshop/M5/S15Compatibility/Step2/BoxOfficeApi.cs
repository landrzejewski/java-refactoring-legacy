using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step2;

/// <summary>Krok 2: bez zmian - Quote(StudentTicket) woła odziedziczone Price().</summary>
public sealed class BoxOfficeApi
{
    public Money Quote(StudentTicket ticket)
    {
        return ticket.Price();
    }
}
