using System.Globalization;
using Training.Workshop.M8.S07Adr.Step1.Notification;
using Training.Workshop.M8.S07Adr.Step1.Pricing;

namespace Training.Workshop.M8.S07Adr.Step1;

/// <summary>Krok 1: warstwa aplikacji przejmuje wysłanie powiadomienia na podstawie Quote.</summary>
public sealed class BookingService
{
    private readonly GroupMailer _mailer = new();
    private readonly TicketPricing _pricing = new();

    public string Book(string organizer, int tickets, string format)
    {
        double unitPrice = format switch
        {
            "IMAX" => 40.00,
            "3D" => 32.00,
            _ => 25.00,
        };
        Quote quote = _pricing.Total(tickets, unitPrice);
        if (quote.GroupDiscount)
        {
            _mailer.GroupDiscountGranted(organizer, tickets);
        }
        return "DO ZAPLATY " + quote.Total.ToString("F2", CultureInfo.InvariantCulture);
    }

    public IReadOnlyList<string> SentMails()
    {
        return _mailer.Sent();
    }
}
