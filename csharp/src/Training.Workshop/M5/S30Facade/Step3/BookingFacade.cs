using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>
/// Krok 3: fasada jako jedyne wejście do podsystemu i strażnik spójności. Podsystemy są
/// internal, a konstruktor fasady też - kanały sprzedaży dostają gotowy obiekt.
/// Niezmiennik "miejsce zajęte tylko po udanej płatności" jest egzekwowany zawsze,
/// także gdy bramka rzuci wyjątkiem (finally). Kod klienta nie może już pominąć kroku,
/// bo nie ma do kroków dostępu.
/// </summary>
public sealed class BookingFacade
{
    private readonly SeatRegistry _seats;
    private readonly PriceList _prices;
    private readonly PaymentClient _payments;
    private readonly TicketMailer _mailer;

    internal BookingFacade(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer)
    {
        _seats = seats;
        _prices = prices;
        _payments = payments;
        _mailer = mailer;
    }

    public string Book(TicketOrder order, Money feePerSeat)
    {
        var total = _prices.Price(order.TicketType).Plus(feePerSeat).Times(order.Seats.Count);
        try
        {
            _seats.Reserve(order.Screening, order.Seats);
        }
        catch (InvalidOperationException exception)
        {
            return "ERROR " + exception.Message;
        }
        var paid = false;
        try
        {
            paid = _payments.Charge(order.Card, total);
        }
        finally
        {
            if (!paid)
            {
                _seats.Release(order.Screening, order.Seats);
            }
        }
        if (!paid)
        {
            return "ERROR payment declined";
        }
        _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
        return "OK " + total;
    }
}
