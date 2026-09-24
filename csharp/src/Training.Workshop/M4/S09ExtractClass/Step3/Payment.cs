namespace Training.Workshop.M4.S09ExtractClass.Step3;

/// <summary>
/// Krok 3: Extract Class - płatność. Własny stan (karta, status), własne reguły
/// (jedna płatność, maskowanie karty) i własny powód zmiany (bramka płatności, PCI).
/// Nie zna Booking: id dostaje jako wartość, więc nie ma referencji zwrotnej ani cyklu.
/// </summary>
public sealed class Payment
{
    private string? _cardNumber;
    private string _status = "NEW";

    public void PayWith(string card, string bookingId)
    {
        if (_status != "NEW")
        {
            throw new InvalidOperationException("Rezerwacja " + bookingId + " jest juz oplacona");
        }
        _cardNumber = card.Replace(" ", "");
        _status = "PAID";
    }

    public bool IsPaid()
    {
        return _status == "PAID";
    }

    public string Description()
    {
        return IsPaid() ? "oplacona karta " + MaskedCard() : "oczekuje";
    }

    private string MaskedCard()
    {
        return "**** " + _cardNumber![^4..];
    }
}
