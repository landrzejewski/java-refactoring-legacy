using Training.Workshop.M6.S14Adapter;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S14Adapter;

/// <summary>Wejście: [dostawca, rezerwacja, kwota]. Obie bramki, sukces i odmowa, nieznany dostawca.</summary>
public sealed class S14EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene = BuildScene();

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPaysTheSameWay(string test) => Scene.Run(test);

    private static Scene<IReadOnlyList<string>, string> BuildScene()
    {
        var xml = new XmlPayGateway();
        var rest = new RestPayClient();
        return Support.Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", Safe(new Training.Workshop.M6.S14Adapter.Start.CheckoutService(xml, rest).Pay))
            .Variant("step1", Safe(new Training.Workshop.M6.S14Adapter.Step1.CheckoutService(xml, rest).Pay))
            .Variant("step2", Safe(new Training.Workshop.M6.S14Adapter.Step2.CheckoutService(xml, rest).Pay))
            .Variant("step3", Safe(new Training.Workshop.M6.S14Adapter.Step3.CheckoutService(xml, rest).Pay))
            .Expect("XML sukces", ["XML", "R1", "40.00"], "OK X-R1")
            .Expect("XML grosze", ["XML", "R2", "0.50"], "OK X-R2")
            .Expect("XML odmowa", ["XML", "R3", "600.00"], "DECLINED 51")
            .Expect("REST sukces", ["REST", "R4", "40.00"], "OK T-R4")
            .Expect("REST odmowa jako wyjątek biblioteki", ["REST", "R5", "600.00"], "DECLINED LIMIT")
            .Expect("granica 500.00 włącznie", ["REST", "R6", "500.00"], "OK T-R6")
            .Expect("nieznany dostawca", ["SWIFT", "R7", "40.00"], "ERROR unknown provider: SWIFT");
    }

    private static Func<IReadOnlyList<string>, string> Safe(Func<string, string, Money, PaymentResult> pay)
    {
        return input =>
        {
            try
            {
                var result = pay(input[0], input[1], Money.Of(input[2]));
                return result.IsAccepted ? "OK " + result.TransactionId : "DECLINED " + result.DeclineCode;
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }
}
