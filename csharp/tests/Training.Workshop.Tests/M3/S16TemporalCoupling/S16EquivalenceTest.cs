using Training.Workshop.M3.S16TemporalCoupling;

namespace Training.Workshop.Tests.M3.S16TemporalCoupling;

/// <summary>Wydruk jest identyczny; znika tylko możliwość złego wywołania.</summary>
public sealed class S16EquivalenceTest
{
    public sealed record Case(Screening Screening, int Seat, string Buyer);

    private static readonly Screening Dune = new("Diuna", "IMAX", new DateTime(2026, 10, 2, 20, 0, 0));

    private static readonly Support.Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", c => new Training.Workshop.M3.S16TemporalCoupling.Start.TicketDesk()
            .Issue(c.Screening, c.Seat, c.Buyer))
        .Variant("step1", c => new Training.Workshop.M3.S16TemporalCoupling.Step1.TicketDesk()
            .Issue(c.Screening, c.Seat, c.Buyer))
        .Variant("step2", c => new Training.Workshop.M3.S16TemporalCoupling.Step2.TicketDesk()
            .Issue(c.Screening, c.Seat, c.Buyer))
        .Expect("Diuna IMAX", new Case(Dune, 14, "Anna@Kino.pl"),
            "BILET Diuna (IMAX) 2026-10-02T20:00, miejsce 14, dla anna@kino.pl")
        .Expect("Kraina Lodu rano",
            new Case(new Screening("Kraina Lodu", "3D", new DateTime(2026, 10, 3, 10, 0, 0)), 3, "jan@kino.pl"),
            "BILET Kraina Lodu (3D) 2026-10-03T10:00, miejsce 3, dla jan@kino.pl");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPrintsTheSameTicket(string test) => Scene.Run(test);

    [Fact]
    public void Step2RejectsIncompleteRequestAtCreation()
    {
        Assert.Throws<ArgumentNullException>(
            () => new Training.Workshop.M3.S16TemporalCoupling.Step2.TicketRequest(Dune, 1, null!));
    }
}
