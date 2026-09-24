namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>
/// Krok 2: Replace Hard-coded Notifications with Observer - odbiorcy jako lista IPaymentListener.
/// Konstruktor bez zmian, lista w starej kolejności, pętla bez try/catch (fail-fast jak w Start).
/// </summary>
public sealed class PaymentService
{
    private readonly IReadOnlyList<IPaymentListener> _listeners;
    private readonly List<string> _paid = [];

    public PaymentService(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
    {
        _listeners =
        [
            new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty),
        ];
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
        foreach (var listener in _listeners)
        {
            listener.OnPaid(paidEvent);
        }
    }

    public IReadOnlyList<string> Paid => _paid.ToList();
}
