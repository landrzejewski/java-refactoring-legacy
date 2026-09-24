namespace Training.Workshop.M4.S09ExtractClass;

/// <summary>Stabilne wejście testu.</summary>
/// <param name="Id">identyfikator rezerwacji</param>
/// <param name="Name">imię i nazwisko klienta</param>
/// <param name="Email">e-mail klienta</param>
/// <param name="Phone">telefon klienta</param>
/// <param name="Amount">kwota rezerwacji</param>
/// <param name="Cards">kolejne próby płatności (każda próba to jedna karta)</param>
public sealed record BookingInput(string Id, string Name, string Email, string Phone, decimal Amount,
    IReadOnlyList<string> Cards);
