package pl.training.module2;

import java.math.BigDecimal;
import java.util.List;

import pl.training.module2.adapter.ConsoleQuoteNotifier;
import pl.training.module2.adapter.InMemoryQuoteRepository;
import pl.training.module2.application.CreateDeliveryQuote;
import pl.training.module2.application.CreateDeliveryQuote.Command;
import pl.training.module2.boyscout.DeploymentResult;
import pl.training.module2.boyscout.DeploymentStatus;
import pl.training.module2.documentation.ConsequenceKind;
import pl.training.module2.documentation.DecisionConsequence;
import pl.training.module2.documentation.DecisionId;
import pl.training.module2.documentation.DecisionOption;
import pl.training.module2.documentation.DecisionRecord;
import pl.training.module2.documentation.DecisionRecordMarkdownRenderer;
import pl.training.module2.documentation.DecisionStatus;
import pl.training.module2.domain.DeliveryPriceCalculator;
import pl.training.module2.domain.DeliveryQuote;
import pl.training.module2.domain.ExpressDeliveryPricePolicy;
import pl.training.module2.domain.FuelSurcharge;
import pl.training.module2.domain.Parcel;
import pl.training.module2.domain.ShippingMethod;
import pl.training.module2.domain.StandardDeliveryPricePolicy;
import pl.training.module2.legacy.LegacyDeliveryQuoteService;
import pl.training.module2.tooling.InMemoryJavaCompiler;
import pl.training.module2.tooling.WarningPolicy;

public final class Module2Examples {
    private Module2Examples() {
    }

    public static void main(String[] args) {
        Parcel parcel = new Parcel(new BigDecimal("3.00"));
        LegacyDeliveryQuoteService legacy = new LegacyDeliveryQuoteService();
        DeliveryQuote legacyQuote = legacy.createQuote(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel);

        FuelSurcharge fuelSurcharge = new FuelSurcharge(
                new BigDecimal("0.08"));
        DeliveryPriceCalculator calculator = new DeliveryPriceCalculator(
                List.of(
                        new StandardDeliveryPricePolicy(fuelSurcharge),
                        new ExpressDeliveryPricePolicy(fuelSurcharge)));
        InMemoryQuoteRepository repository = new InMemoryQuoteRepository();
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator,
                repository,
                new ConsoleQuoteNotifier());

        DeliveryQuote refactoredQuote = useCase.execute(new Command(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel));

        System.out.println(
                "Legacy and refactored prices equal: "
                        + legacyQuote.price().equals(refactoredQuote.price()));
        System.out.println("Stored quotes: " + repository.quotes().size());

        runBoyScoutExample();
        runDecisionRecordExample();
        runCompilerGateExample();
    }

    private static void runBoyScoutExample() {
        var formatter = new pl.training.module2.boyscout.after
                .ReleaseSummaryFormatter();
        String summary = formatter.format("release-42", List.of(
                new DeploymentResult(
                        DeploymentStatus.SUCCESS, "test", "deployed"),
                new DeploymentResult(
                        DeploymentStatus.FAILURE, "prod", "timeout")));

        System.out.println("Boy Scout: " + summary.lines()
                .reduce((first, second) -> second)
                .orElseThrow());
    }

    private static void runDecisionRecordExample() {
        var record = new DecisionRecord(
                new DecisionId("ADR-0042"),
                "Use Branch by Abstraction",
                DecisionStatus.ACCEPTED,
                "The pricing engine must be replaced incrementally.",
                "Route both implementations through one client contract.",
                List.of(
                        new DecisionOption(
                                "Branch by Abstraction",
                                "Supports incremental verification."),
                        new DecisionOption(
                                "Big bang",
                                "Removes coexistence but delays feedback.")),
                List.of(
                        new DecisionConsequence(
                                ConsequenceKind.POSITIVE,
                                "Rollout can stop after each stage."),
                        new DecisionConsequence(
                                ConsequenceKind.NEGATIVE,
                                "Two implementations coexist temporarily.")),
                "Compare results in VERIFY mode before switching traffic.");

        System.out.println("Decision record: " + new DecisionRecordMarkdownRenderer()
                .render(record)
                .lines()
                .findFirst()
                .orElseThrow());
    }

    private static void runCompilerGateExample() {
        String source = """
                package example;

                public final class CleanSample {
                    public int doubleValue(int value) {
                        return value * 2;
                    }
                }
                """;
        boolean successful = new InMemoryJavaCompiler().compile(
                "example.CleanSample",
                source,
                WarningPolicy.TREAT_WARNINGS_AS_ERRORS)
                .successful();

        System.out.println("Compiler gate: compiled=" + successful);
    }
}
