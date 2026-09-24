using Training.Workshop.M6.S07Decorator;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S07Decorator;

/// <summary>Cena i opis (z kolejnością dodatków) takie same w każdym kroku.</summary>
public sealed class S07EquivalenceTest
{
    private static readonly Scene<TicketOrder, string> Scene = Support.Scene.Variants<TicketOrder, string>()
        .Variant("start", o =>
        {
            var t = new Training.Workshop.M6.S07Decorator.Start.TicketAssembler().Assemble(o);
            return t.Description() + " = " + t.Price();
        })
        .Variant("step1", o =>
        {
            var t = new Training.Workshop.M6.S07Decorator.Step1.TicketAssembler().Assemble(o);
            return t.Description() + " = " + t.Price();
        })
        .Variant("step2", o =>
        {
            var t = new Training.Workshop.M6.S07Decorator.Step2.TicketAssembler().Assemble(o);
            return t.Description() + " = " + t.Price();
        })
        .Variant("step3", o =>
        {
            var t = new Training.Workshop.M6.S07Decorator.Step3.TicketAssembler().Assemble(o);
            return t.Description() + " = " + t.Price();
        })
        .Expect("bez dodatków", Order("Amator", "2D", "25.00", false, false, false), "Amator 2D = 25.00")
        .Expect("3D z okularami kina", Order("Kraina Lodu", "3D", "32.00", false, false, false),
            "Kraina Lodu 3D +okulary 3D = 35.00")
        .Expect("3D z własnymi okularami", Order("Kraina Lodu", "3D", "32.00", false, true, false),
            "Kraina Lodu 3D = 32.00")
        .Expect("wszystkie dodatki", Order("Kraina Lodu", "3D", "32.00", true, false, true),
            "Kraina Lodu 3D +VIP +okulary 3D +ubezpieczenie = 49.00")
        .Expect("IMAX VIP z ubezpieczeniem", Order("Diuna", "IMAX", "40.00", true, false, true),
            "Diuna IMAX +VIP +ubezpieczenie = 54.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesEmbellishmentsTheSame(string test) => Scene.Run(test);

    private static TicketOrder Order(
        string title, string format, string basePrice, bool vip, bool ownGlasses, bool insurance)
    {
        return new TicketOrder(title, format, Money.Of(basePrice), vip, ownGlasses, insurance);
    }
}
