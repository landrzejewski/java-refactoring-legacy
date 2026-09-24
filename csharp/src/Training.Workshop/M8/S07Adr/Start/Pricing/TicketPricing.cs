using Training.Workshop.M8.S07Adr.Start.Notification;

namespace Training.Workshop.M8.S07Adr.Start.Pricing;

/// <summary>
/// Start: cennik narusza oba punkty ADR-0007 - sam wysyła powiadomienie (zależność
/// Pricing -> Notification) i liczy kwoty w double.
/// </summary>
public sealed class TicketPricing
{
    private readonly GroupMailer _mailer;

    public TicketPricing(GroupMailer mailer)
    {
        _mailer = mailer;
    }

    public double Total(string organizer, int tickets, double unitPrice)
    {
        double sum = unitPrice * tickets;
        if (tickets >= 10)
        {
            sum = sum - sum * 0.10;
            _mailer.GroupDiscountGranted(organizer, tickets);
        }
        return Math.Floor(sum * 100 + 0.5) / 100.0;
    }
}
