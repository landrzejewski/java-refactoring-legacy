using Training.Module8.Documentation;

namespace Training.Module8.Tests.Documentation;

public sealed class DecisionRecordTest
{
    [Fact]
    public void SnapshotsCollectionsAndExposesImmutableViews()
    {
        var options = new List<DecisionOption>
        {
            new("Port", "Separates the domain from time access")
        };
        var consequences = new List<DecisionConsequence>
        {
            new(ConsequenceKind.Positive, "Tests can supply a deterministic clock")
        };

        DecisionRecord record = Record(options, consequences);
        options.Clear();
        consequences.Clear();

        Assert.Single(record.ConsideredOptions);
        Assert.Single(record.Consequences);
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<DecisionOption>)record.ConsideredOptions).Clear());
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<DecisionConsequence>)record.Consequences).Clear());
    }

    [Fact]
    public void RejectsInvalidIdentifiersAndIncompleteRecords()
    {
        Assert.Throws<ArgumentNullException>(() => new DecisionId(null!));
        Assert.Throws<ArgumentException>(() => new DecisionId("42"));

        Assert.Throws<ArgumentException>(
            () => new DecisionRecord(
                new DecisionId("ADR-0042"),
                " ",
                DecisionStatus.Proposed,
                "Context",
                "Decision",
                [new DecisionOption("Option", "Rationale")],
                [new DecisionConsequence(
                    ConsequenceKind.Neutral,
                    "Consequence")],
                "Run tests"));
        Assert.Throws<ArgumentException>(
            () => Record([], [new DecisionConsequence(
                ConsequenceKind.Neutral,
                "Consequence")]));
        Assert.Throws<ArgumentException>(
            () => Record([new DecisionOption("Option", "Rationale")], []));
        Assert.Throws<ArgumentException>(
            () => new DecisionOption("Option", " "));
        Assert.Throws<ArgumentException>(
            () => new DecisionConsequence(ConsequenceKind.Positive, " "));
    }

    private static DecisionRecord Record(
        IReadOnlyList<DecisionOption> options,
        IReadOnlyList<DecisionConsequence> consequences) =>
        new(
            new DecisionId("ADR-0042"),
            "Use a clock port",
            DecisionStatus.Accepted,
            "The domain reads system time directly.",
            "Introduce a clock port at the application boundary.",
            options,
            consequences,
            "Run characterization tests before and after the change.");
}
