namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>Krok 2: obserwator - potwierdzenie mailem.</summary>
public sealed record MailConfirmation(IMailer Mailer) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Mailer.Send(paidEvent.Email,
            "Potwierdzenie platnosci " + paidEvent.ReservationId + ": " + paidEvent.Amount);
    }
}
