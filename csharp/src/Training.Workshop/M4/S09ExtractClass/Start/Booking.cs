using System.Globalization;
using System.Text.RegularExpressions;

namespace Training.Workshop.M4.S09ExtractClass.Start;

/// <summary>
/// Start: rezerwacja, która wie wszystko - o kliencie (imię, e-mail, telefon i ich formatowanie)
/// i o płatności (karta, status, maskowanie). Trzy powody zmiany w jednej klasie.
/// </summary>
public sealed class Booking
{
    private readonly string _id;
    private readonly string _customerName;
    private readonly string _customerEmail;
    private readonly string _customerPhone;
    private readonly decimal _amount;
    private string? _cardNumber;
    private string _paymentStatus = "NEW";

    public Booking(string id, string customerName, string customerEmail, string customerPhone,
        decimal amount)
    {
        _id = id;
        _customerName = customerName;
        _customerEmail = customerEmail;
        _customerPhone = customerPhone;
        _amount = amount;
    }

    public string Contact()
    {
        return _customerName + " <" + _customerEmail.Trim().ToLowerInvariant() + ">, tel. "
            + FormattedPhone();
    }

    public void Pay(string card)
    {
        if (_paymentStatus != "NEW")
        {
            throw new InvalidOperationException("Rezerwacja " + _id + " jest juz oplacona");
        }
        _cardNumber = card.Replace(" ", "");
        _paymentStatus = "PAID";
    }

    public bool IsPaid()
    {
        return _paymentStatus == "PAID";
    }

    public string Summary()
    {
        return "Rezerwacja " + _id + "\n"
            + "Klient: " + Contact() + "\n"
            + "Kwota: " + _amount.ToString(CultureInfo.InvariantCulture) + "\n"
            + "Platnosc: " + (IsPaid() ? "oplacona karta " + MaskedCard() : "oczekuje") + "\n";
    }

    private string FormattedPhone()
    {
        string digits = Regex.Replace(_customerPhone, @"\D", "");
        string local = digits[^9..];
        return local[..3] + "-" + local[3..6] + "-" + local[6..];
    }

    private string MaskedCard()
    {
        return "**** " + _cardNumber![^4..];
    }
}
