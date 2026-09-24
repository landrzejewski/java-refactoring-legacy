using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S15BehaviourVector;

/// <summary>
/// Słaby test z punktu startu: obserwuje tylko wynik. Jest zielony dla KAŻDEGO wariantu -
/// także dla start i step1, które wysyłają "Bilety oplacone" po odrzuceniu karty.
/// </summary>
public sealed class S15ResultOnlyTest
{
    private static readonly Scene<Payment, string> Scene = Support.Scene.Variants<Payment, string>()
        .Variant("start", p => new Training.Workshop.M7.S15BehaviourVector.Start.TicketCheckout()
            .Pay(p.Booking(), p.Card))
        .Variant("step1", p => new Training.Workshop.M7.S15BehaviourVector.Step1.TicketCheckout()
            .Pay(p.Booking(), p.Card))
        .Variant("step2", p => new Training.Workshop.M7.S15BehaviourVector.Step2.TicketCheckout()
            .Pay(p.Booking(), p.Card))
        .Variant("step3", p => new Training.Workshop.M7.S15BehaviourVector.Step3.TicketCheckout()
            .Pay(p.Booking(), p.Card))
        .Expect("sukces", Payment.Success, "OK")
        .Expect("karta odrzucona", Payment.Declined, "DECLINED")
        .Expect("juz oplacona", Payment.AlreadyPaid, "ERROR: status Paid");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void ResultOnlyCannotSeeTheRegression(string test) => Scene.Run(test);
}
