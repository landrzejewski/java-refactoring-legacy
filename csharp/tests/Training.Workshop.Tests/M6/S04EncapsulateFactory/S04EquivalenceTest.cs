using Training.Workshop.M6.S04EncapsulateFactory;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S04EncapsulateFactory;

/// <summary>Sprzedaż pojedyncza i grupowa daje te same bilety w każdym kroku.</summary>
public sealed class S04EquivalenceTest
{
    private static readonly Scene<SeatSale, string> Scene = Support.Scene.Variants<SeatSale, string>()
        .Variant("start", s =>
        {
            var office = new Training.Workshop.M6.S04EncapsulateFactory.Start.BoxOffice();
            return office.Sell(s.Title, s.Base, s.Rows[0]).Describe() + " | "
                + string.Join("; ", office.SellAll(s.Title, s.Base, s.Rows).Select(t => t.Describe()));
        })
        .Variant("step1", s =>
        {
            var office = new Training.Workshop.M6.S04EncapsulateFactory.Step1.BoxOffice();
            return office.Sell(s.Title, s.Base, s.Rows[0]).Describe() + " | "
                + string.Join("; ", office.SellAll(s.Title, s.Base, s.Rows).Select(t => t.Describe()));
        })
        .Variant("step2", s =>
        {
            var office = new Training.Workshop.M6.S04EncapsulateFactory.Step2.BoxOffice();
            return office.Sell(s.Title, s.Base, s.Rows[0]).Describe() + " | "
                + string.Join("; ", office.SellAll(s.Title, s.Base, s.Rows).Select(t => t.Describe()));
        })
        .Variant("step3", s =>
        {
            var office = new Training.Workshop.M6.S04EncapsulateFactory.Step3.BoxOffice();
            return office.Sell(s.Title, s.Base, s.Rows[0]).Describe() + " | "
                + string.Join("; ", office.SellAll(s.Title, s.Base, s.Rows).Select(t => t.Describe()));
        })
        .Expect("IMAX, granica VIP na rzędzie 10",
            new SeatSale("Diuna", Money.Of("40.00"), [9, 10, 12]),
            "Diuna r9 40.00 | Diuna r9 40.00; Diuna r10 VIP 50.00; Diuna r12 VIP 50.00")
        .Expect("2D, pierwsze miejsce VIP",
            new SeatSale("Amator", Money.Of("25.00"), [11, 1]),
            "Amator r11 VIP 35.00 | Amator r11 VIP 35.00; Amator r1 25.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSellsTheSameTickets(string test) => Scene.Run(test);
}
