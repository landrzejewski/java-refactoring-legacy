namespace Training.Workshop.M6.S09Observer.Start;

/// <summary>
/// Start: po opłaceniu serwis na sztywno woła mail, SMS i program lojalnościowy. Każdy nowy
/// odbiorca to zmiana w tej klasie. Semantyka do zachowania: kolejność i fail-fast
/// (wyjątek w SMS przerywa - punkty nie zostaną naliczone, a rezerwacja jest już opłacona).
/// </summary>
public sealed class PaymentService
{
    private readonly IMailer _mailer;
    private readonly ISmsGateway _sms;
    private readonly ILoyaltyProgram _loyalty;
    private readonly List<string> _paid = [];

    public PaymentService(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
    {
        _mailer = mailer;
        _sms = sms;
        _loyalty = loyalty;
    }

    public void Confirm(Payment payment)
    {
        if (payment.Amount.Amount <= 0)
        {
            throw new ArgumentException("amount must be positive");
        }
        _paid.Add(payment.ReservationId);
        _mailer.Send(payment.Email,
            "Potwierdzenie platnosci " + payment.ReservationId + ": " + payment.Amount);
        _sms.Send(payment.Phone, "Oplacono " + payment.ReservationId);
        _loyalty.AddPoints(payment.Email, (int)payment.Amount.Amount / 10);
    }

    public IReadOnlyList<string> Paid => _paid.ToList();
}
