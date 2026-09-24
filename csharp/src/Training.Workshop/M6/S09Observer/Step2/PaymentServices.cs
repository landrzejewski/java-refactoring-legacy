namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>Krok 2: bez zmian - korzeń kompozycji.</summary>
public static class PaymentServices
{
    public static PaymentService Standard(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
    {
        return new PaymentService(mailer, sms, loyalty);
    }
}
