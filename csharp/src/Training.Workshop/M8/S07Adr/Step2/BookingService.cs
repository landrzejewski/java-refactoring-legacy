using Training.Workshop.M8.S07Adr.Step2.Notification;
using Training.Workshop.M8.S07Adr.Step2.Pricing;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S07Adr.Step2;

/// <summary>Krok 2: warstwa aplikacji podaje cenę jednostkową jako Money.</summary>
public sealed class BookingService
{
    private readonly GroupMailer _mailer = new();
    private readonly TicketPricing _pricing = new();

    public string Book(string organizer, int tickets, string format)
    {
        Money unitPrice = format switch
        {
            "IMAX" => Money.Of("40.00"),
            "3D" => Money.Of("32.00"),
            _ => Money.Of("25.00"),
        };
        Quote quote = _pricing.Total(tickets, unitPrice);
        if (quote.GroupDiscount)
        {
            _mailer.GroupDiscountGranted(organizer, tickets);
        }
        return "DO ZAPLATY " + quote.Total;
    }

    public IReadOnlyList<string> SentMails()
    {
        return _mailer.Sent();
    }
}
