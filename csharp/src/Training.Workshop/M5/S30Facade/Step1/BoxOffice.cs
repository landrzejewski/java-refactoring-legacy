using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step1;

/// <summary>
/// Krok 1: bez zmian - kasa nadal orkiestruje podsystemy sama. Zestawienie jej metody
/// z BookingFacade.Book pokazuje różnicę: przy odmowie płatności kasa nie zwalnia miejsc.
/// </summary>
public sealed class BoxOffice
{
    private readonly SeatRegistry _seats;
    private readonly PriceList _prices;
    private readonly PaymentClient _payments;
    private readonly TicketMailer _mailer;

    public BoxOffice(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer)
    {
        _seats = seats;
        _prices = prices;
        _payments = payments;
        _mailer = mailer;
    }

    public string Sell(TicketOrder order)
    {
        var total = _prices.Price(order.TicketType).Times(order.Seats.Count);
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
            return "ERROR payment declined";
        }
        _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
        return "OK " + total;
    }
}
