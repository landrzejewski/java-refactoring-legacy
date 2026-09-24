using System.Globalization;
using Training.Workshop.M8.S07Adr.Start.Notification;
using Training.Workshop.M8.S07Adr.Start.Pricing;

namespace Training.Workshop.M8.S07Adr.Start;

/// <summary>Start: warstwa aplikacji - składa cennik z powiadomieniami i formatuje odpowiedź.</summary>
public sealed class BookingService
{
    private readonly GroupMailer _mailer;
    private readonly TicketPricing _pricing;

    public BookingService()
    {
        _mailer = new GroupMailer();
        _pricing = new TicketPricing(_mailer);
    }

    public string Book(string organizer, int tickets, string format)
    {
        double unitPrice = format switch
        {
            "IMAX" => 40.00,
            "3D" => 32.00,
            _ => 25.00,
        };
        double total = _pricing.Total(organizer, tickets, unitPrice);
        return "DO ZAPLATY " + total.ToString("F2", CultureInfo.InvariantCulture);
    }

    public IReadOnlyList<string> SentMails()
    {
        return _mailer.Sent();
    }
}
