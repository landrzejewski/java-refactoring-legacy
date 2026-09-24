using Training.Workshop.M6.S09Observer;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S09Observer;

/// <summary>Kolejność powiadomień i polityka błędów (fail-fast) takie same w każdym kroku.</summary>
public sealed class S09EquivalenceTest
{
    /// <summary>Failing: "mail", "sms", "loyalty" albo "" - który odbiorca rzuca wyjątek.</summary>
    public sealed record Case(Payment Payment, string Failing);

    private sealed record Service(Action<Payment> Confirm, Func<IReadOnlyList<string>> Paid);

    private static readonly Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", c => Play(c, p =>
        {
            var s = Training.Workshop.M6.S09Observer.Start.PaymentServices.Standard(p, p, p);
            return new Service(s.Confirm, () => s.Paid);
        }))
        .Variant("step1", c => Play(c, p =>
        {
            var s = Training.Workshop.M6.S09Observer.Step1.PaymentServices.Standard(p, p, p);
            return new Service(s.Confirm, () => s.Paid);
        }))
        .Variant("step2", c => Play(c, p =>
        {
            var s = Training.Workshop.M6.S09Observer.Step2.PaymentServices.Standard(p, p, p);
            return new Service(s.Confirm, () => s.Paid);
        }))
        .Variant("step3", c => Play(c, p =>
        {
            var s = Training.Workshop.M6.S09Observer.Step3.PaymentServices.Standard(p, p, p);
            return new Service(s.Confirm, () => s.Paid);
        }))
        .Expect("wszyscy odbiorcy w kolejności", new Case(Payment("95.50"), ""), """
            mail anna@kino.pl: Potwierdzenie platnosci R1: 95.50
            sms 600100200: Oplacono R1
            points anna@kino.pl +9
            paid=[R1]
            """)
        .Expect("wyjątek w SMS: mail wysłany, punkty nie, opłata zapisana", new Case(Payment("40.00"), "sms"), """
            mail anna@kino.pl: Potwierdzenie platnosci R1: 40.00
            ERROR sms down
            paid=[R1]
            """)
        .Expect("wyjątek w mailu przerywa wszystko", new Case(Payment("40.00"), "mail"), """
            ERROR mail down
            paid=[R1]
            """)
        .Expect("kwota niedodatnia - nikt nie dostaje powiadomienia", new Case(Payment("0.00"), ""), """
            ERROR amount must be positive
            paid=[]
            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepNotifiesTheSameWay(string test) => Scene.Run(test);

    private static Payment Payment(string amount)
    {
        return new Payment("R1", "anna@kino.pl", "600100200", Money.Of(amount));
    }

    private static string Play(Case c, Func<Ports, Service> factory)
    {
        var log = new List<string>();
        var service = factory(new Ports(c, log));
        try
        {
            service.Confirm(c.Payment);
        }
        catch (Exception exception) when (exception is InvalidOperationException or ArgumentException)
        {
            log.Add("ERROR " + exception.Message);
        }
        log.Add("paid=[" + string.Join(", ", service.Paid()) + "]");
        return string.Join("\n", log);
    }

    /// <summary>Fałszywe porty (w Javie trzy lambdy) zapisujące do wspólnego logu.</summary>
    private sealed class Ports(Case c, List<string> log) : IMailer, ISmsGateway, ILoyaltyProgram
    {
        void IMailer.Send(string to, string text)
        {
            FailIf("mail");
            log.Add("mail " + to + ": " + text);
        }

        void ISmsGateway.Send(string phone, string text)
        {
            FailIf("sms");
            log.Add("sms " + phone + ": " + text);
        }

        public void AddPoints(string email, int points)
        {
            FailIf("loyalty");
            log.Add("points " + email + " +" + points);
        }

        private void FailIf(string channel)
        {
            if (c.Failing == channel)
            {
                throw new InvalidOperationException(channel + " down");
            }
        }
    }
}
