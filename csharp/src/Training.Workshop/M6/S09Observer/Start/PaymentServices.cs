namespace Training.Workshop.M6.S09Observer.Start;

/// <summary>Start: korzeń kompozycji aplikacji - tu powstaje serwis płatności.</summary>
public static class PaymentServices
{
    public static PaymentService Standard(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
    {
        return new PaymentService(mailer, sms, loyalty);
    }
}
