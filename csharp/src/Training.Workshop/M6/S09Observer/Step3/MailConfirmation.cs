namespace Training.Workshop.M6.S09Observer.Step3;

/// <summary>Krok 3: obserwator - potwierdzenie mailem.</summary>
public sealed record MailConfirmation(IMailer Mailer) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Mailer.Send(paidEvent.Email,
            "Potwierdzenie platnosci " + paidEvent.ReservationId + ": " + paidEvent.Amount);
    }
}
