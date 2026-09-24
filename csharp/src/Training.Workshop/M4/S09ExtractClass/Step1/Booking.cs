using System.Globalization;
using System.Text.RegularExpressions;

namespace Training.Workshop.M4.S09ExtractClass.Step1;

/// <summary>
/// Krok 1: pola klienta przeniesione do <see cref="Customer"/> (Move Field x3). Logika nadal tutaj
/// i sięga po dane przez właściwości: <c>_customer.Email</c>, <c>_customer.Phone</c>.
/// Konstruktor i publiczne API bez zmian - klienci Booking nic nie zauważyli.
/// </summary>
public sealed class Booking
{
    private readonly string _id;
    private readonly Customer _customer;
    private readonly decimal _amount;
    private string? _cardNumber;
    private string _paymentStatus = "NEW";

    public Booking(string id, string customerName, string customerEmail, string customerPhone,
        decimal amount)
    {
        _id = id;
        _customer = new Customer(customerName, customerEmail, customerPhone);
        _amount = amount;
    }

    public string Contact()
    {
        return _customer.Name + " <" + _customer.Email.Trim().ToLowerInvariant() + ">, tel. "
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
        string digits = Regex.Replace(_customer.Phone, @"\D", "");
        string local = digits[^9..];
        return local[..3] + "-" + local[3..6] + "-" + local[6..];
    }

    private string MaskedCard()
    {
        return "**** " + _cardNumber![^4..];
    }
}
