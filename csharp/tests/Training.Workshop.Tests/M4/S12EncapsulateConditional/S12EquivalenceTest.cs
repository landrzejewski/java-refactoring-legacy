using Training.Workshop.M4.S12EncapsulateConditional;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S12EncapsulateConditional;

/// <summary>Test równoważności: ta sama kwota zwrotu - oba boki progu 24 h, start seansu, promocje, null.</summary>
public sealed class S12EquivalenceTest
{
    private static readonly DateTime Start = new(2026, 9, 25, 20, 0, 0);
    private static readonly Money Seventy = Money.Of("70.00");

    public sealed record Refund(Booking Booking, DateTime Now);

    private static readonly Scene<Refund, string> Scene = Support.Scene.Variants<Refund, string>()
        .Variant("start", r => new Training.Workshop.M4.S12EncapsulateConditional.Start
            .RefundCalculator().Refund(r.Booking, r.Now).ToString())
        .Variant("step1", r => new Training.Workshop.M4.S12EncapsulateConditional.Step1
            .RefundCalculator().Refund(r.Booking, r.Now).ToString())
        .Variant("step2", r => new Training.Workshop.M4.S12EncapsulateConditional.Step2
            .RefundCalculator().Refund(r.Booking, r.Now).ToString())
        .Variant("step3", r => new Training.Workshop.M4.S12EncapsulateConditional.Step3
            .RefundCalculator().Refund(r.Booking, r.Now).ToString())
        .Expect("dokładnie 24 h przed - 100% minus 3.00",
            new Refund(new Booking("PAID", Start, Seventy, null), Start.AddHours(-24)), "67.00")
        .Expect("24 h minus minuta - 50% minus 3.00",
            new Refund(new Booking("PAID", Start, Seventy, null), Start.AddHours(-24).AddMinutes(1)),
            "32.00")
        .Expect("w chwili startu - brak zwrotu",
            new Refund(new Booking("PAID", Start, Seventy, null), Start), "0.00")
        .Expect("nieopłacona - brak zwrotu",
            new Refund(new Booking("NEW", Start, Seventy, null), Start.AddDays(-3)), "0.00")
        .Expect("darmowy bilet z promocji - brak zwrotu",
            new Refund(new Booking("PAID", Start, Seventy, "FREE-100"), Start.AddDays(-3)), "0.00")
        .Expect("inna promocja - zwrot jak zwykle",
            new Refund(new Booking("PAID", Start, Seventy, "STUDENT"), Start.AddDays(-3)), "67.00")
        .Expect("tani bilet: 50% z 5.00 minus 3.00 nie schodzi poniżej zera",
            new Refund(new Booking("PAID", Start, Money.Of("5.00"), null), Start.AddHours(-2)), "0.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepRefundsTheSameAmount(string test) => Scene.Run(test);
}
