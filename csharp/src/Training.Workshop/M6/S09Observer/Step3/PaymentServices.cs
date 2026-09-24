namespace Training.Workshop.M6.S09Observer.Step3;

/// <summary>Krok 3: korzeń kompozycji - tu (i tylko tu) ustalamy zestaw i kolejność odbiorców.</summary>
public static class PaymentServices
{
    public static PaymentService Standard(IMailer mailer, ISmsGateway sms, ILoyaltyProgram loyalty)
    {
        var service = new PaymentService();
        service.Subscribe(new MailConfirmation(mailer));
        service.Subscribe(new SmsConfirmation(sms));
        service.Subscribe(new LoyaltyPoints(loyalty));
        return service;
    }
}
