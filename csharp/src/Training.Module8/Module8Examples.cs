using System.Collections.ObjectModel;
using Training.Module8.BoyScout;
using Training.Module8.Collaboration;
using Training.Module8.Documentation;
using Training.Module8.Incremental;
using Training.Module8.Risk;
using Training.Module8.Tooling;

namespace Training.Module8;

public static class Module8Examples
{
    public static IReadOnlyList<string> RunExamples() =>
        new ReadOnlyCollection<string>(
        [
            IncrementalMigrationExample(),
            BoyScoutExample(),
            CollaborationExample(),
            DocumentationExample(),
            ToolingExample(),
            RiskExample()
        ]);

    private static string IncrementalMigrationExample()
    {
        var events = new List<VerificationEvent>();
        var engine = new MigratingPricingEngine(
            new LegacyPricingEngineAdapter(),
            new CandidatePricingEngine(),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        var quote = engine.Quote(new PriceRequest(
            19.99m,
            3,
            0.10m));

        string verificationEvent = events[0].GetType().Name;
        return "Stopniowa migracja: "
            + FormatMoney(quote.NetAmount) + "/" + verificationEvent;
    }

    private static string BoyScoutExample()
    {
        var formatter = new BoyScout.After.ReleaseSummaryFormatter();
        string summary = formatter.Format("release-42",
        [
            new DeploymentResult(
                DeploymentStatus.Success, "test", "deployed"),
            new DeploymentResult(
                DeploymentStatus.Failure, "prod", "timeout")
        ]);

        return "Boy Scout: " + summary.Split('\n')[^1];
    }

    private static string CollaborationExample()
    {
        var changeSet = new ChangeSet(
            "Extract deployment clock",
            [ChangeIntent.Refactoring],
            [new VerificationEvidence(
                EvidenceKind.AutomatedTest,
                "Characterization suite passed")],
            true);

        bool ready = new ExampleTeamReviewPolicy()
            .Assess(changeSet)
            .Ready;
        // Java wypisuje boolean jako "true"/"false"; C# bool.ToString() daje "True".
        return "Code review: ready=" + (ready ? "true" : "false");
    }

    private static string DocumentationExample()
    {
        var record = new DecisionRecord(
            new DecisionId("ADR-0042"),
            "Use Branch by Abstraction",
            DecisionStatus.Accepted,
            "The pricing engine must be replaced incrementally.",
            "Route both implementations through one client contract.",
            [
                new DecisionOption(
                    "Branch by Abstraction",
                    "Supports incremental verification."),
                new DecisionOption(
                    "Big bang",
                    "Removes coexistence but delays feedback.")
            ],
            [
                new DecisionConsequence(
                    ConsequenceKind.Positive,
                    "Rollout can stop after each stage."),
                new DecisionConsequence(
                    ConsequenceKind.Negative,
                    "Two implementations coexist temporarily.")
            ],
            "Compare results in VERIFY mode before switching traffic.");

        string heading = new DecisionRecordMarkdownRenderer()
            .Render(record)
            .Split('\n')[0];
        return "Dokumentowanie: " + heading;
    }

    private static string ToolingExample()
    {
        const string source = """
            namespace Example;

            public sealed class CleanSample
            {
                public int DoubleValue(int value)
                {
                    return value * 2;
                }
            }
            """;
        bool successful = new InMemoryCSharpCompiler().Compile(
                "Example.CleanSample",
                source,
                WarningPolicy.TreatWarningsAsErrors)
            .Successful;

        return "Narzędzia: compiled=" + (successful ? "true" : "false");
    }

    private static string RiskExample()
    {
        var policy = new RolloutPolicy(
            new RolloutThresholds(100, 0.05, 250.0));
        var decision = policy.Decide(
            new RolloutSnapshot(200, 4, 0, 180.0));
        // Nazwy stałych enum w Javie są pisane wielkimi literami (ADVANCE).
        return "Zarządzanie ryzykiem: " + decision.ToString().ToUpperInvariant();
    }

    private static string FormatMoney(decimal amount) =>
        amount.ToString("0.00", System.Globalization.CultureInfo.InvariantCulture);

    public static void Main(string[] args)
    {
        foreach (string result in RunExamples())
        {
            Console.WriteLine(result);
        }
    }
}
