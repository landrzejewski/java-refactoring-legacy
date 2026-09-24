using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S15BehaviourVector;

/// <summary>
/// Wektor obserwowalnego zachowania rośnie razem z seamami:
/// krok 1 - wynik + maile (regresja widoczna), krok 2 - naprawa,
/// krok 3 - wynik, wyjątek, maile i obciążenia w jednej kolejności, stan rezerwacji.
/// </summary>
public sealed class S15BehaviourVectorTest
{
    private static readonly Scene<Payment, string> Mails = Support.Scene.Variants<Payment, string>()
        .Variant("step2", p => WithMails(p, (mailer, payment) =>
            new Training.Workshop.M7.S15BehaviourVector.Step2.TicketCheckout(mailer.Add)
                .Pay(payment.Booking(), payment.Card)))
        .Variant("step3", p => WithMails(p, (mailer, payment) =>
            new Training.Workshop.M7.S15BehaviourVector.Step3.TicketCheckout(
                    mailer.Add, (card, amount) => !card.EndsWith("0000", StringComparison.Ordinal))
                .Pay(payment.Booking(), payment.Card)))
        .Expect("sukces", Payment.Success, "OK [anna@kino.pl: Bilety B1 oplacone: 114.00]")
        .Expect("karta odrzucona", Payment.Declined, "DECLINED [anna@kino.pl: Platnosc odrzucona B1]")
        .Expect("juz oplacona", Payment.AlreadyPaid, "ERROR: status Paid []");

    private static readonly Scene<Payment, string> FullVectorScene = Support.Scene.Variants<Payment, string>()
        .Variant("step3", FullVector)
        .Expect("sukces: najpierw obciazenie, potem mail, status PAID", Payment.Success,
            "wynik=OK; zdarzenia=[CHARGE 4111111111111111 114.00, "
                + "MAIL anna@kino.pl: Bilety B1 oplacone: 114.00]; status=Paid")
        .Expect("odrzucenie: status bez zmian", Payment.Declined,
            "wynik=DECLINED; zdarzenia=[CHARGE 4111111111110000 114.00, "
                + "MAIL anna@kino.pl: Platnosc odrzucona B1]; status=New")
        .Expect("juz oplacona: zadnych efektow", Payment.AlreadyPaid,
            "wynik=ERROR: status Paid; zdarzenia=[]; status=Paid")
        .Expect("brak karty: typ i komunikat wyjatku, zadnych efektow", Payment.NoCard,
            "wyjatek=ArgumentNullException: card; zdarzenia=[]; status=New");

    public static TheoryData<string> MailCases => Mails.Tests();

    public static TheoryData<string> FullVectorCases => FullVectorScene.Tests();

    [Fact]
    public void Step1SeesTheRegressionThatResultOnlyTestMissed()
    {
        Assert.Equal("DECLINED [anna@kino.pl: Platnosc odrzucona B1, anna@kino.pl: Bilety B1 oplacone: 114.00]",
            WithMails(Payment.Declined, (mailer, p) =>
                new Training.Workshop.M7.S15BehaviourVector.Step1.TicketCheckout(mailer.Add)
                    .Pay(p.Booking(), p.Card)));
    }

    [Theory]
    [MemberData(nameof(MailCases))]
    public void FromStep2MailsAreCorrect(string test) => Mails.Run(test);

    [Theory]
    [MemberData(nameof(FullVectorCases))]
    public void Step3ObservesTheFullVector(string test) => FullVectorScene.Run(test);

    /// <summary>Wynik + lista maili (dostępne od kroku 1).</summary>
    private static string WithMails(Payment payment, Func<MailLog, Payment, string> pay)
    {
        var mails = new MailLog();
        var result = pay(mails, payment);
        return result + " [" + string.Join(", ", mails.Entries) + "]";
    }

    /// <summary>Pełny wektor: wynik albo wyjątek, efekty w kolejności, stan po operacji.</summary>
    private static string FullVector(Payment payment)
    {
        var events = new List<string>();
        var checkout = new Training.Workshop.M7.S15BehaviourVector.Step3.TicketCheckout(
            (to, text) => events.Add("MAIL " + to + ": " + text),
            (card, amount) =>
            {
                events.Add("CHARGE " + card + " " + amount);
                return !card.EndsWith("0000", StringComparison.Ordinal);
            });
        var booking = payment.Booking();
        string Vector(string outcome) =>
            outcome + "; zdarzenia=[" + string.Join(", ", events) + "]; status=" + booking.Status;
        try
        {
            return Vector("wynik=" + checkout.Pay(booking, payment.Card));
        }
        catch (Exception e)
        {
            // ArgumentNullException niesie nazwę parametru osobno (ParamName) - jak komunikat requireNonNull w Javie
            var message = e is ArgumentNullException nullArgument ? nullArgument.ParamName : e.Message;
            return Vector("wyjatek=" + e.GetType().Name + ": " + message);
        }
    }

    private sealed class MailLog
    {
        public List<string> Entries { get; } = [];

        public void Add(string to, string text)
        {
            Entries.Add(to + ": " + text);
        }
    }
}
