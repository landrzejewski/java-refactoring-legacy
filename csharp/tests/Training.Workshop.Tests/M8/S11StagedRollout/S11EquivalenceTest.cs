using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S11StagedRollout;

/// <summary>Test równoważności: z dotychczasowymi ustawieniami każdy krok kieruje klientów tak samo.</summary>
public sealed class S11EquivalenceTest
{
    private static readonly Scene<string, bool> Scene = Support.Scene.Variants<string, bool>()
        .Variant("start", new Training.Workshop.M8.S11StagedRollout.Start.CheckoutRouter().UseNewCheckout)
        .Variant("step1", new Training.Workshop.M8.S11StagedRollout.Step1.CheckoutRouter().UseNewCheckout)
        .Variant("step2", new Training.Workshop.M8.S11StagedRollout.Step2.CheckoutRouter().UseNewCheckout)
        .Variant("step3", new Training.Workshop.M8.S11StagedRollout.Step3.CheckoutRouter().UseNewCheckout)
        .Expect("tester Anna", "anna@kino.pl", true)
        .Expect("tester Jan", "jan@kino.pl", true)
        .Expect("klientka Ola", "ola@kino.pl", false)
        .Expect("klient Piotr", "piotr@kino.pl", false);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void CurrentSettingsRouteTheSameCustomers(string test) => Scene.Run(test);
}
