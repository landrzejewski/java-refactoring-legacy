using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step3;

/// <summary>
/// Krok 3: Generalize Parameter Type (Change Signature) - Quote przyjmuje każdy Ticket.
/// Zgodne ŹRÓDŁOWO (stary kod po rekompilacji działa), niezgodne BINARNIE: sygnatura
/// Quote(StudentTicket) zniknęła z metadanych, więc stara wtyczka dostanie MissingMethodException.
/// </summary>
public sealed class BoxOfficeApi
{
    public Money Quote(Ticket ticket)
    {
        return ticket.Price();
    }
}
