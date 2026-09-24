namespace Training.Workshop.M6.S09Observer.Step1;

/// <summary>Krok 1: bez zmian - korzeń kompozycji.</summary>
public static class PaymentServices
{
    public static PaymentService Standard(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
    {
        return new PaymentService(mailer, sms, loyalty);
    }
}
