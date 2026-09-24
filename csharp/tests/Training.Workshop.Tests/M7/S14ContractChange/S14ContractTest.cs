using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S14ContractChange;

/// <summary>
/// Część przypadków nie zmienia się nigdy. Dwa przypadki brzegowe pokazują, który krok
/// zmienił kontrakt: krok 2 zmienił format i zaokrąglenie, krok 3 świadomie tylko zaokrąglenie.
/// </summary>
public sealed class S14ContractTest
{
    public sealed record Cancel(double TicketsPaid, long MinutesBeforeStart);

    private static readonly Cancel HalfCent = new(64.35, 120);
    private static readonly Cancel AfterStart = new(114.00, -10);

    private static readonly Scene<Cancel, string> Scene = Support.Scene.Variants<Cancel, string>()
        .Variant("start", Start)
        .Variant("step1", Step1)
        .Variant("step2", Step2)
        .Variant("step3", Step3)
        .Expect(">= 24h: 100% - 3.00", new Cancel(114.00, 48 * 60), "111.00")
        .Expect("dokladnie 24h", new Cancel(114.00, 24 * 60), "111.00")
        .Expect("< 24h: 50% - 3.00", new Cancel(104.40, 10 * 60), "49.20")
        .Expect("< 24h, kwota z groszami", new Cancel(153.00, 30), "73.50");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void OrdinaryRefundsNeverChange(string test) => Scene.Run(test);

    [Fact]
    public void RefactoringStepKeepsHistoricalRoundingAndFormat()
    {
        Assert.Equal("29.17", Start(HalfCent));
        Assert.Equal("29.17", Step1(HalfCent));
        Assert.Equal("0.00", Start(AfterStart));
        Assert.Equal("0.00", Step1(AfterStart));
    }

    [Fact]
    public void ByTheWayStepChangedTwoThingsAtOnce()
    {
        // zaokraglenie: AwayFromZero (HALF_UP) na dokladnej wartosci 29.175
        Assert.Equal("29.18", Step2(HalfCent));
        // format: 0m zamiast 0.00
        Assert.Equal("0", Step2(AfterStart));
    }

    [Fact]
    public void DeliberateStepChangesOnlyTheApprovedRounding()
    {
        // zatwierdzona zmiana kontraktu
        Assert.Equal("29.18", Step3(HalfCent));
        // format przywrocony
        Assert.Equal("0.00", Step3(AfterStart));
    }

    private static string Start(Cancel c) =>
        new Training.Workshop.M7.S14ContractChange.Start.RefundCalculator().Refund(c.TicketsPaid, c.MinutesBeforeStart);

    private static string Step1(Cancel c) =>
        new Training.Workshop.M7.S14ContractChange.Step1.RefundCalculator().Refund(c.TicketsPaid, c.MinutesBeforeStart);

    private static string Step2(Cancel c) =>
        new Training.Workshop.M7.S14ContractChange.Step2.RefundCalculator().Refund(c.TicketsPaid, c.MinutesBeforeStart);

    private static string Step3(Cancel c) =>
        new Training.Workshop.M7.S14ContractChange.Step3.RefundCalculator().Refund(c.TicketsPaid, c.MinutesBeforeStart);
}
