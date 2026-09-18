using System.Xml;
using Training.Module6.Adapter.After;
using Training.Module6.Command.After;
using Training.Module6.Composite.After;
using Training.Module6.Decorator.After;
using Training.Module6.Factory.After;
using Training.Module6.Observer.After;
using Training.Module6.Polymorphism.After;
using Training.Module6.Singleton;
using Training.Module6.State.After;
using Training.Module6.Strategy.After;
using Training.Module6.TemplateMethod.After;
using Training.Module6.TypeCode.After;
using ExtractedPlanNodes = Training.Module6.ExtractComposite.After.PlanNodes;
using LegacyPlanNodes = Training.Module6.ExtractComposite.Before.LegacyPlanNodes;

namespace Training.Module6;

public static class Module6Examples
{
    public static void Main(string[] args)
    {
        var cost = new DeploymentCostCalculator(new ExpeditedCostPolicy())
            .Calculate(10_000);

        IReadOnlyList<string> stepResults =
        [
            new ScriptStep("deploy.sh").Execute(),
            new ApprovalStep("anna").Execute()
        ];

        var request = new DeploymentRequest(
            "rel-42", DeploymentZone.FromCode("prod"));

        var plan = PlanBuilder.Group("release")
            .Group("database", group => group
                .Task("backup", 5)
                .Task("migrate", 8))
            .Task("deploy", 3)
            .Build();

        var probeFactory = new DeploymentProbeFactory();
        var probe = probeFactory
            .Create(DeploymentProbeFactory.ProbeKind.Http, "/health")
            .Check();

        var audit = new List<string>();
        var deployment = new AuditedDeploymentRunner(
                new BasicDeploymentRunner(), audit.Add)
            .Run("rel-42");

        var release = new Release();
        release.Approve();
        release.Deploy();

        var events = new List<string>();
        var publisher = new ReleasePublisher();
        publisher.Subscribe(@event => events.Add("audit:" + @event.ReleaseId));
        publisher.Subscribe(@event => events.Add("metric:" + @event.ReleaseId));
        publisher.Publish(new ReleasePublished("rel-42"));

        var notifications = new NotificationService(
            new LegacyGatewayAdapter(
                (destination, body) => destination + "|" + body));
        var notification = notifications.Notify(
            new ReleaseMessage("ops", "rel-42"));

        var commands = new DeploymentCommandDispatcher(
            new Dictionary<DeploymentAction, IDeploymentCommand>
            {
                [DeploymentAction.Pause] = new PauseDeployment(),
                [DeploymentAction.Rollback] = new RollbackDeployment()
            });

        var imported = new KeyValueReleaseImporter()
            .ImportRelease("id=rel-42;service=payments");

        var legacyGroup = new LegacyPlanNodes.ReleaseGroup();
        legacyGroup.Add(new LegacyPlanNodes.TaskNode(13));
        var extractedGroup = new ExtractedPlanNodes.ReleaseGroup();
        extractedGroup.Add(new ExtractedPlanNodes.TaskNode(13));

        // Formatting helpers reproduce Java's toString() conventions
        // (List -> [a, b], boolean -> true, enum name, record, Duration ISO-8601).
        Console.WriteLine("Strategy cost: " + cost);
        Console.WriteLine("Polymorphic steps: " + Format(stepResults));
        Console.WriteLine("Type object requires approval: " + Format(request.RequiresApproval()));
        Console.WriteLine("Composite total: " + plan.TotalMinutes());
        Console.WriteLine("Visitor total: " + plan.Accept(new TotalMinutesVisitor()));
        Console.WriteLine("Factory probe: " + probe);
        Console.WriteLine("Decorated deployment: " + deployment + ", audit=" + Format(audit));
        Console.WriteLine("State: " + release.Status.ToString().ToUpperInvariant());
        Console.WriteLine("Observer events: " + Format(events));
        Console.WriteLine("Adapter result: " + notification);
        Console.WriteLine("Command result: "
            + commands.Dispatch(DeploymentAction.Rollback, "rel-42"));
        Console.WriteLine("Template result: ReleaseDraft[releaseId=" + imported.ReleaseId
            + ", service=" + imported.Service + "]");
        Console.WriteLine("Singleton timeout: "
            + XmlConvert.ToString(DeploymentDefaults.Instance.HealthCheckTimeout));
        Console.WriteLine("Extract Composite equivalent: "
            + Format(legacyGroup.TotalMinutes() == extractedGroup.TotalMinutes()));
    }

    private static string Format(IEnumerable<string> values) =>
        "[" + string.Join(", ", values) + "]";

    private static string Format(bool value) => value ? "true" : "false";
}
