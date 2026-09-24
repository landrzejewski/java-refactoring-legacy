using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step1;

/// <summary>Krok 1: bez zmian.</summary>
public sealed class BoxOfficeApi
{
    public Money Quote(StudentTicket ticket)
    {
        return ticket.Price();
    }
}
