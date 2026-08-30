package pl.training.module8;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import pl.training.module8.boyscout.DeploymentResult;
import pl.training.module8.boyscout.DeploymentStatus;
import pl.training.module8.collaboration.ChangeIntent;
import pl.training.module8.collaboration.ChangeSet;
import pl.training.module8.collaboration.EvidenceKind;
import pl.training.module8.collaboration.ExampleTeamReviewPolicy;
import pl.training.module8.collaboration.VerificationEvidence;
import pl.training.module8.documentation.ConsequenceKind;
import pl.training.module8.documentation.DecisionConsequence;
import pl.training.module8.documentation.DecisionId;
import pl.training.module8.documentation.DecisionOption;
import pl.training.module8.documentation.DecisionRecord;
import pl.training.module8.documentation.DecisionRecordMarkdownRenderer;
import pl.training.module8.documentation.DecisionStatus;
import pl.training.module8.incremental.CandidatePricingEngine;
import pl.training.module8.incremental.LegacyPricingEngineAdapter;
import pl.training.module8.incremental.MigratingPricingEngine;
import pl.training.module8.incremental.MigrationMode;
import pl.training.module8.incremental.PriceRequest;
import pl.training.module8.incremental.VerificationEvent;
import pl.training.module8.risk.RolloutPolicy;
import pl.training.module8.risk.RolloutSnapshot;
import pl.training.module8.risk.RolloutThresholds;
import pl.training.module8.tooling.InMemoryJavaCompiler;
import pl.training.module8.tooling.WarningPolicy;

public final class Module8Examples {
    private Module8Examples() {
    }

    public static List<String> runExamples() {
        return List.of(
                incrementalMigrationExample(),
                boyScoutExample(),
                collaborationExample(),
                documentationExample(),
                toolingExample(),
                riskExample());
    }

    private static String incrementalMigrationExample() {
        var events = new ArrayList<VerificationEvent>();
        var engine = new MigratingPricingEngine(
                new LegacyPricingEngineAdapter(),
                new CandidatePricingEngine(),
                events::add,
                MigrationMode.VERIFY);

        var quote = engine.quote(new PriceRequest(
                new BigDecimal("19.99"),
                3,
                new BigDecimal("0.10")));

        String event = events.getFirst().getClass().getSimpleName();
        return "Stopniowa migracja: " + quote.netAmount() + "/" + event;
    }

    private static String boyScoutExample() {
        var formatter = new pl.training.module8.boyscout.after
                .ReleaseSummaryFormatter();
        String summary = formatter.format("release-42", List.of(
                new DeploymentResult(
                        DeploymentStatus.SUCCESS, "test", "deployed"),
                new DeploymentResult(
                        DeploymentStatus.FAILURE, "prod", "timeout")));

        return "Boy Scout: " + summary.lines().reduce((first, second) -> second)
                .orElseThrow();
    }

    private static String collaborationExample() {
        var changeSet = new ChangeSet(
                "Extract deployment clock",
                EnumSet.of(ChangeIntent.REFACTORING),
                List.of(new VerificationEvidence(
                        EvidenceKind.AUTOMATED_TEST,
                        "Characterization suite passed")),
                true);

        boolean ready = new ExampleTeamReviewPolicy()
                .assess(changeSet)
                .ready();
        return "Code review: ready=" + ready;
    }

    private static String documentationExample() {
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

        String heading = new DecisionRecordMarkdownRenderer()
                .render(record)
                .lines()
                .findFirst()
                .orElseThrow();
        return "Dokumentowanie: " + heading;
    }

    private static String toolingExample() {
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

        return "Narzędzia: compiled=" + successful;
    }

    private static String riskExample() {
        var policy = new RolloutPolicy(
                new RolloutThresholds(100, 0.05, 250.0));
        var decision = policy.decide(
                new RolloutSnapshot(200, 4, 0, 180.0));
        return "Zarządzanie ryzykiem: " + decision;
    }

    public static void main(String[] args) {
        runExamples().forEach(System.out::println);
    }
}
