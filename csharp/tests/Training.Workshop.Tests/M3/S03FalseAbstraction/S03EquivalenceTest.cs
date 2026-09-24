using System.Globalization;

namespace Training.Workshop.Tests.M3.S03FalseAbstraction;

/// <summary>Rozbicie fałszywej abstrakcji nie zmienia cen biletów ani karnetów.</summary>
public sealed class S03EquivalenceTest
{
    public sealed record TicketCase(string Format, bool Morning, bool OwnGlasses);

    private static readonly Support.Scene<TicketCase, string> Tickets = Support.Scene.Variants<TicketCase, string>()
        .Variant("start", c => Show(new Training.Workshop.M3.S03FalseAbstraction.Start.TicketCounter()
            .Ticket(c.Format, c.Morning, c.OwnGlasses)))
        .Variant("step1", c => Show(new Training.Workshop.M3.S03FalseAbstraction.Step1.TicketCounter()
            .Ticket(c.Format, c.Morning, c.OwnGlasses)))
        .Variant("step2", c => Show(new Training.Workshop.M3.S03FalseAbstraction.Step2.TicketCounter()
            .Ticket(c.Format, c.Morning, c.OwnGlasses)))
        .Expect("3D rano, bez wlasnych okularow", new TicketCase("3D", true, false), "30.00")
        .Expect("3D wieczorem, wlasne okulary", new TicketCase("3D", false, true), "32.00")
        .Expect("IMAX wieczorem", new TicketCase("IMAX", false, false), "40.00")
        .Expect("2D rano", new TicketCase("2D", true, false), "20.00");

    private static readonly Support.Scene<int, string> Passes = Support.Scene.Variants<int, string>()
        .Variant("start", n => Show(new Training.Workshop.M3.S03FalseAbstraction.Start.PassCounter().Pass(n)))
        .Variant("step1", n => Show(new Training.Workshop.M3.S03FalseAbstraction.Step1.PassCounter().Pass(n)))
        .Variant("step2", n => Show(new Training.Workshop.M3.S03FalseAbstraction.Step2.PassCounter().Pass(n)))
        .Expect("karnet na 10 wejsc", 10, "200.00")
        .Expect("karnet na 5 wejsc", 5, "100.00");

    public static TheoryData<string> TicketCases => Tickets.Tests();

    public static TheoryData<string> PassCases => Passes.Tests();

    [Theory]
    [MemberData(nameof(TicketCases))]
    public void TicketsCostTheSame(string test) => Tickets.Run(test);

    [Theory]
    [MemberData(nameof(PassCases))]
    public void PassesCostTheSame(string test) => Passes.Run(test);

    private static string Show(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
