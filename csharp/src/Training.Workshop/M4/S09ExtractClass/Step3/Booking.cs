using System.Globalization;

namespace Training.Workshop.M4.S09ExtractClass.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Booking składa rezerwację z <see cref="Customer"/> i <see cref="Payment"/>.
/// Publiczne API (konstruktor, Contact, Pay, IsPaid, Summary) bez zmian - to fasada.
/// </summary>
public sealed class Booking
{
    private readonly string _id;
    private readonly Customer _customer;
    private readonly decimal _amount;
    private readonly Payment _payment = new();

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
        _payment.PayWith(card, _id);
    }

    public bool IsPaid()
    {
        return _payment.IsPaid();
    }

    public string Summary()
    {
        return "Rezerwacja " + _id + "\n"
            + "Klient: " + Contact() + "\n"
            + "Kwota: " + _amount.ToString(CultureInfo.InvariantCulture) + "\n"
            + "Platnosc: " + _payment.Description() + "\n";
    }
}
