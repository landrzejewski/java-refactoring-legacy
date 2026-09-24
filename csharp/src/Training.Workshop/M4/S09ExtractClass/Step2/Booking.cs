using System.Globalization;

namespace Training.Workshop.M4.S09ExtractClass.Step2;

/// <summary>
/// Krok 2: <c>Contact()</c> deleguje do <see cref="Customer.ContactLine"/> - zostaje jako fasada,
/// bo jest częścią publicznego API rezerwacji. Płatność nadal wymieszana z rezerwacją.
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
        return _customer.ContactLine();
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

    private string MaskedCard()
    {
        return "**** " + _cardNumber![^4..];
    }
}
