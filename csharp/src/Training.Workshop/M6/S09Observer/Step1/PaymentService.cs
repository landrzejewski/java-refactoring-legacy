namespace Training.Workshop.M6.S09Observer.Step1;

/// <summary>Krok 1: Extract Method NotifyPaid + obiekt zdarzenia. Powiadomienia nadal na sztywno.</summary>
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
        NotifyPaid(new ReservationPaid(
            payment.ReservationId, payment.Email, payment.Phone, payment.Amount));
    }

    private void NotifyPaid(ReservationPaid paidEvent)
    {
        _mailer.Send(paidEvent.Email,
            "Potwierdzenie platnosci " + paidEvent.ReservationId + ": " + paidEvent.Amount);
        _sms.Send(paidEvent.Phone, "Oplacono " + paidEvent.ReservationId);
        _loyalty.AddPoints(paidEvent.Email, (int)paidEvent.Amount.Amount / 10);
    }

    public IReadOnlyList<string> Paid => _paid.ToList();
}
