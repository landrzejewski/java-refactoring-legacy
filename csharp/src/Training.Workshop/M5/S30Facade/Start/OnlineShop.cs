using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Start;

/// <summary>
/// Start: sklep internetowy sam orkiestruje cztery podsystemy w ustalonej kolejności:
/// cena, rezerwacja miejsc, płatność (odmowa zwalnia miejsca), mail z biletem.
/// Do tego opłata rezerwacyjna 2.00 za miejsce.
/// </summary>
public sealed class OnlineShop
{
    private static readonly Money BookingFee = Money.Of("2.00");

    private readonly SeatRegistry _seats;
    private readonly PriceList _prices;
    private readonly PaymentClient _payments;
    private readonly TicketMailer _mailer;

    public OnlineShop(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer)
    {
        _seats = seats;
        _prices = prices;
        _payments = payments;
        _mailer = mailer;
    }

    public string Buy(TicketOrder order)
    {
        var total = _prices.Price(order.TicketType).Plus(BookingFee).Times(order.Seats.Count);
        try
        {
            _seats.Reserve(order.Screening, order.Seats);
        }
        catch (InvalidOperationException exception)
        {
            return "ERROR " + exception.Message;
        }
        if (!_payments.Charge(order.Card, total))
        {
            _seats.Release(order.Screening, order.Seats);
            return "ERROR payment declined";
        }
        _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
        return "OK " + total;
    }
}
