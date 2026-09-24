using System.Globalization;

namespace Training.Workshop.Tests.M3.S02Similarity;

/// <summary>Rozdzielenie fałszywie scalonych opłat nie zmienia żadnej kwoty.</summary>
public sealed class S02EquivalenceTest
{
    public sealed record Case(IReadOnlyList<decimal> TicketPrices, decimal Paid, int RefundPercent);

    private static readonly Support.Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", c => "online " + Show(new Training.Workshop.M3.S02Similarity.Start.OnlineCheckout()
            .Total(c.TicketPrices)) + " / zwrot " + Show(new Training.Workshop.M3.S02Similarity.Start
            .RefundDesk().Refund(c.Paid, c.RefundPercent)))
        .Variant("step1", c => "online " + Show(new Training.Workshop.M3.S02Similarity.Step1.OnlineCheckout()
            .Total(c.TicketPrices)) + " / zwrot " + Show(new Training.Workshop.M3.S02Similarity.Step1
            .RefundDesk().Refund(c.Paid, c.RefundPercent)))
        .Variant("step2", c => "online " + Show(new Training.Workshop.M3.S02Similarity.Step2.OnlineCheckout()
            .Total(c.TicketPrices)) + " / zwrot " + Show(new Training.Workshop.M3.S02Similarity.Step2
            .RefundDesk().Refund(c.Paid, c.RefundPercent)))
        .Variant("step3", c => "online " + Show(new Training.Workshop.M3.S02Similarity.Step3.OnlineCheckout()
            .Total(c.TicketPrices)) + " / zwrot " + Show(new Training.Workshop.M3.S02Similarity.Step3
            .RefundDesk().Refund(c.Paid, c.RefundPercent)))
        .Expect("dwa bilety, pelny zwrot", Of("65.00", 100, "40.00", "25.00"),
            "online 69.00 / zwrot 62.00")
        .Expect("jeden bilet, zwrot 50%", Of("19.00", 50, "19.00"),
            "online 21.00 / zwrot 6.50")
        .Expect("zwrot po starcie nie schodzi ponizej zera", Of("17.50", 0, "17.50"),
            "online 19.50 / zwrot 0.00")
        .Expect("potracenie wieksze niz polowa ceny", Of("5.00", 50, "25.00", "25.00", "25.00"),
            "online 81.00 / zwrot 0.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepChargesAndRefundsTheSame(string test) => Scene.Run(test);

    private static Case Of(string paid, int percent, params string[] prices) =>
        new(prices.Select(Parse).ToList(), Parse(paid), percent);

    private static decimal Parse(string amount) => decimal.Parse(amount, CultureInfo.InvariantCulture);

    private static string Show(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
