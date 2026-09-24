namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>Krok 2: obserwator - potwierdzenie SMS.</summary>
public sealed record SmsConfirmation(ISmsGateway Sms) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Sms.Send(paidEvent.Phone, "Oplacono " + paidEvent.ReservationId);
    }
}
