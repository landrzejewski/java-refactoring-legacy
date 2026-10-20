using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step2;

/// <summary>
/// Krok 2: fasada obsługuje oba kanały. Kasa dostała w prezencie kompensację, której
/// jej kopia nie miała - odmowa płatności w kasie zwalnia teraz miejsca. To zmiana zachowania
/// (naprawa błędu), udokumentowana osobnym testem, a nie "czysta" refaktoryzacja.
/// </summary>
public sealed class BookingFacade
{
    private readonly SeatRegistry _seats;
    private readonly PriceList _prices;
    private readonly PaymentClient _payments;
    private readonly TicketMailer _mailer;

    public BookingFacade(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer)
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
        if (!_payments.Charge(order.Card, total))
        {
            _seats.Release(order.Screening, order.Seats);
            return "ERROR payment declined";
        }
        _mailer.Send(order.Email, "Bilety " + order.Screening + " " + string.Join(",", order.Seats) + " = " + total);
        return "OK " + total;
    }
}
