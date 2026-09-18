using System.Globalization;
using Training.Module7.Arrowhead;
using Training.Module7.Arrowhead.After;
using Training.Module7.BooleanParameter.After;
using Training.Module7.BreakDependencies.After;
using Training.Module7.BreakMethod;
using Training.Module7.BreakMethod.After;
using Training.Module7.BreakResponsibilities;
using Training.Module7.BreakResponsibilities.After;
using Training.Module7.Contract.After;
using Training.Module7.DoubleNegative.After;
using Training.Module7.Duplication.After;
using Training.Module7.GodClass.After;
using Training.Module7.MethodObject;
using Training.Module7.MethodObject.After;
using Training.Module7.MiddleMan;
using Training.Module7.ParameterObject.After;
using Training.Module7.ReturnAsap;
using Training.Module7.ReturnAsap.After;

namespace Training.Module7;

public static class Module7Examples
{
    public static IReadOnlyList<string> RunExamples()
    {
        var results = new List<string>();

        var windowService = new DeploymentWindowService(new StandardMaintenanceWindows());
        results.Add("Break Dependencies: "
            + Display(windowService.Schedule("payments", 2)));

        var risk = new DeploymentRiskCalculator().Calculate(
            new DeploymentRiskInput(10, 2, 1, true));
        results.Add("Extract Method Object: "
            + risk.Score.ToString(CultureInfo.InvariantCulture) + "/" + Display(risk.Level));

        var reportService = new DeploymentReportService(
            new DeploymentMetricsCalculator(),
            new DeploymentReportFormatter());
        results.Add("Break Responsibilities: " + reportService.Generate(
        [
            new DeploymentSample(12, true),
            new DeploymentSample(18, false),
            new DeploymentSample(24, true)
        ]));

        results.Add("Remove Duplication: "
            + new ArtifactPublisher().PublishRelease(" IMAGE ", 42));

        var manifest = new ReleaseManifestBuilder().Build(
        [
            new ManifestEntry("worker", "sha-worker", 20),
            new ManifestEntry("api", "sha-api", 10)
        ]);
        results.Add("Break Method: " + manifest.Replace('\n', ','));

        var rolloutSeconds = new RolloutPlanner().EstimateSeconds(
            new RolloutSpec("payments", "eu-central-1", 10, 3, 15));
        results.Add("Introduce Parameter Object: "
            + rolloutSeconds.ToString(CultureInfo.InvariantCulture) + "s");

        results.Add("Remove Arrowhead: " + Display(new DeploymentEligibility().Evaluate(
            new DeploymentCandidate("rel-42", true, true, true))));

        var capacity = new DeploymentCapacity(10);
        capacity.Reserve(3);
        results.Add("Design by Contract: remaining="
            + capacity.Remaining.ToString(CultureInfo.InvariantCulture));

        var canRelease = new ReleaseGate().CanRelease(new ReleaseReadiness(true, true, true));
        results.Add("Remove Double Negative: " + (canRelease ? "true" : "false"));

        var repository = new InMemoryReleaseRepository();
        var auditTrail = new AuditTrail();
        var notifier = new ReleaseNotifier();
        var releaseService = new ReleaseApplicationService(
            new ReleaseValidator(), repository, auditTrail, notifier);
        var release = releaseService.Publish("rel-42", "payments", "2.1.0");
        results.Add(string.Create(
            CultureInfo.InvariantCulture,
            $"Remove God Class: {release.ReleaseId}/{repository.FindAll().Count}/{auditTrail.Entries().Count}/{notifier.Notifications().Count}"));

        results.Add("Remove Boolean Parameter: " + new DeploymentExecutor().Deploy("rel-42"));

        var registry = new MiddleMan.After.DeploymentRegistry();
        registry.Update("dep-42", DeploymentStatus.Running);
        var dashboard = new MiddleMan.After.ReleaseDashboard(registry);
        results.Add("Remove Middle Man: " + dashboard.Render("dep-42"));

        var artifact = new ArtifactFinder().FindByChecksum(
            [
                new Artifact("api.jar", "sha-1"),
                new Artifact("worker.jar", "sha-2")
            ],
            "sha-1") ?? throw new InvalidOperationException("No value present");
        results.Add("Return ASAP: " + artifact.Name);

        return results.AsReadOnly();
    }

    public static void Main(string[] args)
    {
        foreach (var result in RunExamples())
        {
            Console.WriteLine(result);
        }
    }

    // Java prints enum constants in UPPER_CASE; C# enum members are PascalCase.
    private static string Display(Enum value) => value.ToString().ToUpperInvariant();
}
