package pl.training.module7;

import java.util.ArrayList;
import java.util.List;

import pl.training.module7.arrowhead.DeploymentCandidate;
import pl.training.module7.arrowhead.after.DeploymentEligibility;
import pl.training.module7.booleanparameter.after.DeploymentExecutor;
import pl.training.module7.breakdependencies.after.DeploymentWindowService;
import pl.training.module7.breakdependencies.after.StandardMaintenanceWindows;
import pl.training.module7.breakmethod.ManifestEntry;
import pl.training.module7.breakmethod.after.ReleaseManifestBuilder;
import pl.training.module7.breakresponsibilities.DeploymentSample;
import pl.training.module7.breakresponsibilities.after.DeploymentMetricsCalculator;
import pl.training.module7.breakresponsibilities.after.DeploymentReportFormatter;
import pl.training.module7.breakresponsibilities.after.DeploymentReportService;
import pl.training.module7.contract.after.DeploymentCapacity;
import pl.training.module7.doublenegative.after.ReleaseGate;
import pl.training.module7.doublenegative.after.ReleaseReadiness;
import pl.training.module7.duplication.after.ArtifactPublisher;
import pl.training.module7.godclass.after.AuditTrail;
import pl.training.module7.godclass.after.InMemoryReleaseRepository;
import pl.training.module7.godclass.after.ReleaseApplicationService;
import pl.training.module7.godclass.after.ReleaseNotifier;
import pl.training.module7.godclass.after.ReleaseValidator;
import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.after.DeploymentRiskCalculator;
import pl.training.module7.middleman.DeploymentStatus;
import pl.training.module7.parameterobject.after.RolloutPlanner;
import pl.training.module7.parameterobject.after.RolloutSpec;
import pl.training.module7.returnasap.Artifact;
import pl.training.module7.returnasap.after.ArtifactFinder;

public final class Module7Examples {
    private Module7Examples() {
    }

    public static List<String> runExamples() {
        List<String> results = new ArrayList<>();

        var windowService = new DeploymentWindowService(
                new StandardMaintenanceWindows());
        results.add("Break Dependencies: "
                + windowService.schedule("payments", 2));

        var risk = new DeploymentRiskCalculator().calculate(
                new DeploymentRiskInput(10, 2, 1, true));
        results.add("Extract Method Object: "
                + risk.score() + "/" + risk.level());

        var reportService = new DeploymentReportService(
                new DeploymentMetricsCalculator(),
                new DeploymentReportFormatter());
        results.add("Break Responsibilities: " + reportService.generate(List.of(
                new DeploymentSample(12, true),
                new DeploymentSample(18, false),
                new DeploymentSample(24, true))));

        results.add("Remove Duplication: "
                + new ArtifactPublisher().publishRelease(" IMAGE ", 42));

        String manifest = new ReleaseManifestBuilder().build(List.of(
                new ManifestEntry("worker", "sha-worker", 20),
                new ManifestEntry("api", "sha-api", 10)));
        results.add("Break Method: " + manifest.replace('\n', ','));

        long rolloutSeconds = new RolloutPlanner().estimateSeconds(
                new RolloutSpec("payments", "eu-central-1", 10, 3, 15));
        results.add("Introduce Parameter Object: " + rolloutSeconds + "s");

        results.add("Remove Arrowhead: "
                + new DeploymentEligibility().evaluate(
                        new DeploymentCandidate("rel-42", true, true, true)));

        var capacity = new DeploymentCapacity(10);
        capacity.reserve(3);
        results.add("Design by Contract: remaining=" + capacity.remaining());

        boolean canRelease = new ReleaseGate().canRelease(
                new ReleaseReadiness(true, true, true));
        results.add("Remove Double Negative: " + canRelease);

        var repository = new InMemoryReleaseRepository();
        var auditTrail = new AuditTrail();
        var notifier = new ReleaseNotifier();
        var releaseService = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);
        var release = releaseService.publish("rel-42", "payments", "2.1.0");
        results.add("Remove God Class: " + release.releaseId()
                + "/" + repository.findAll().size()
                + "/" + auditTrail.entries().size()
                + "/" + notifier.notifications().size());

        results.add("Remove Boolean Parameter: "
                + new DeploymentExecutor().deploy("rel-42"));

        var registry = new pl.training.module7.middleman.after
                .DeploymentRegistry();
        registry.update("dep-42", DeploymentStatus.RUNNING);
        var dashboard = new pl.training.module7.middleman.after
                .ReleaseDashboard(registry);
        results.add("Remove Middle Man: " + dashboard.render("dep-42"));

        var artifact = new ArtifactFinder().findByChecksum(
                List.of(
                        new Artifact("api.jar", "sha-1"),
                        new Artifact("worker.jar", "sha-2")),
                "sha-1").orElseThrow();
        results.add("Return ASAP: " + artifact.name());

        return List.copyOf(results);
    }

    public static void main(String[] args) {
        runExamples().forEach(System.out::println);
    }
}
