using Training.Module2.Adapter;
using Training.Module2.Application;
using Training.Module2.BoyScout;
using Training.Module2.Documentation;
using Training.Module2.Domain;
using Training.Module2.Legacy;
using Training.Module2.Tooling;
using Command = Training.Module2.Application.CreateDeliveryQuote.Command;

namespace Training.Module2;

public static class Module2Examples
{
    public static void Main(string[] args)
    {
        Parcel parcel = new(3.00m);
        LegacyDeliveryQuoteService legacy = new();
        DeliveryQuote legacyQuote = legacy.CreateQuote(
            "developer@example.com",
            ShippingMethod.Standard,
            parcel);

        FuelSurcharge fuelSurcharge = new(0.08m);
        DeliveryPriceCalculator calculator = new(
        [
            new StandardDeliveryPricePolicy(fuelSurcharge),
            new ExpressDeliveryPricePolicy(fuelSurcharge),
        ]);
        InMemoryQuoteRepository repository = new();
        CreateDeliveryQuote useCase = new(
            calculator,
            repository,
            new ConsoleQuoteNotifier());

        DeliveryQuote refactoredQuote = useCase.Execute(new Command(
            "developer@example.com",
            ShippingMethod.Standard,
            parcel));

        Console.WriteLine(
            "Legacy and refactored prices equal: "
            + (legacyQuote.Price == refactoredQuote.Price).ToString().ToLowerInvariant());
        Console.WriteLine("Stored quotes: " + repository.Quotes().Count);

        RunBoyScoutExample();
        RunDecisionRecordExample();
        RunCompilerGateExample();
    }

    private static void RunBoyScoutExample()
    {
        var formatter = new BoyScout.After.ReleaseSummaryFormatter();
        string summary = formatter.Format("release-42",
        [
            new DeploymentResult(
                DeploymentStatus.Success, "test", "deployed"),
            new DeploymentResult(
                DeploymentStatus.Failure, "prod", "timeout")
        ]);

        Console.WriteLine("Boy Scout: " + summary.Split('\n')[^1]);
    }

    private static void RunDecisionRecordExample()
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

        Console.WriteLine("Decision record: " + new DecisionRecordMarkdownRenderer()
            .Render(record)
            .Split('\n')[0]);
    }

    private static void RunCompilerGateExample()
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

        // Java wypisuje boolean jako "true"/"false"; C# bool.ToString() daje "True".
        Console.WriteLine("Compiler gate: compiled=" + (successful ? "true" : "false"));
    }
}
