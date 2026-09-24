using Training.Workshop.M8.S05BoyScout;

namespace Training.Workshop.Tests.M8.S05BoyScout;

/// <summary>Test różnicowy demaskuje "sprzątanie", które zmieniło zachowanie.</summary>
public sealed class S05SolutionTest
{
    private readonly IReadOnlyList<(string Name, Ticket Ticket)> _cases =
    [
        ("zwykły bilet", S05EquivalenceTest.Regular),
        ("miejsca w rzędach 9 i 10", S05EquivalenceTest.Rows9And10),
        ("brak telefonu", S05EquivalenceTest.NoPhone),
        ("e-mail z wielkimi literami", S05EquivalenceTest.MixedCaseEmail),
    ];

    [Fact]
    public void AbusiveCleanupChangesBehaviourInThreeCases()
    {
        var before = new Training.Workshop.M8.S05BoyScout.Start.TicketPrinter();
        var abuse = new Training.Workshop.M8.S05BoyScout.Step1.TicketPrinter();
        var changed = new List<string>();
        foreach (var (name, ticket) in _cases)
        {
            if (!before.Print(ticket).Equals(abuse.Print(ticket)))
            {
                changed.Add(name);
            }
        }
        Assert.Equal(["miejsca w rzędach 9 i 10", "brak telefonu", "e-mail z wielkimi literami"], changed);
    }

    [Fact]
    public void AbusiveCleanupSortsSeatsLexicographically()
    {
        string printed = new Training.Workshop.M8.S05BoyScout.Step1.TicketPrinter()
            .Print(S05EquivalenceTest.Rows9And10);
        Assert.Equal("Miejsca: A10, A9", printed.Split('\n').First(l => l.StartsWith("Miejsca", StringComparison.Ordinal)));
    }

    [Fact]
    public void CorrectBoyScoutStepChangesNothingObservable()
    {
        var before = new Training.Workshop.M8.S05BoyScout.Start.TicketPrinter();
        var after = new Training.Workshop.M8.S05BoyScout.Step2.TicketPrinter();
        foreach (var (_, ticket) in _cases)
        {
            Assert.Equal(before.Print(ticket), after.Print(ticket));
        }
    }
}
