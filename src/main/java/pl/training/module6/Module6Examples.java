package pl.training.module6;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.training.module6.adapter.after.LegacyGatewayAdapter;
import pl.training.module6.adapter.after.NotificationService;
import pl.training.module6.adapter.after.ReleaseMessage;
import pl.training.module6.command.after.DeploymentAction;
import pl.training.module6.command.after.DeploymentCommandDispatcher;
import pl.training.module6.command.after.PauseDeployment;
import pl.training.module6.command.after.RollbackDeployment;
import pl.training.module6.composite.after.DeploymentGroup;
import pl.training.module6.composite.after.PlanBuilder;
import pl.training.module6.composite.after.TotalMinutesVisitor;
import pl.training.module6.decorator.after.AuditedDeploymentRunner;
import pl.training.module6.decorator.after.BasicDeploymentRunner;
import pl.training.module6.factory.after.DeploymentProbeFactory;
import pl.training.module6.observer.after.ReleasePublished;
import pl.training.module6.observer.after.ReleasePublisher;
import pl.training.module6.polymorphism.after.ApprovalStep;
import pl.training.module6.polymorphism.after.ScriptStep;
import pl.training.module6.singleton.DeploymentDefaults;
import pl.training.module6.state.after.Release;
import pl.training.module6.strategy.after.DeploymentCostCalculator;
import pl.training.module6.strategy.after.ExpeditedCostPolicy;
import pl.training.module6.templatemethod.after.KeyValueReleaseImporter;
import pl.training.module6.typecode.after.DeploymentRequest;
import pl.training.module6.typecode.after.DeploymentZone;

public final class Module6Examples {
    private Module6Examples() {
    }

    public static void main(String[] args) {
        long cost = new DeploymentCostCalculator(new ExpeditedCostPolicy())
                .calculate(10_000);

        List<String> stepResults = List.of(
                new ScriptStep("deploy.sh").execute(),
                new ApprovalStep("anna").execute());

        DeploymentRequest request = new DeploymentRequest(
                "rel-42", DeploymentZone.fromCode("prod"));

        DeploymentGroup plan = PlanBuilder.group("release")
                .group("database", group -> group
                        .task("backup", 5)
                        .task("migrate", 8))
                .task("deploy", 3)
                .build();

        DeploymentProbeFactory probeFactory = new DeploymentProbeFactory();
        String probe = probeFactory
                .create(DeploymentProbeFactory.ProbeKind.HTTP, "/health")
                .check();

        List<String> audit = new ArrayList<>();
        String deployment = new AuditedDeploymentRunner(
                new BasicDeploymentRunner(), audit::add)
                .run("rel-42");

        Release release = new Release();
        release.approve();
        release.deploy();

        List<String> events = new ArrayList<>();
        ReleasePublisher publisher = new ReleasePublisher();
        publisher.subscribe(event -> events.add("audit:" + event.releaseId()));
        publisher.subscribe(event -> events.add("metric:" + event.releaseId()));
        publisher.publish(new ReleasePublished("rel-42"));

        NotificationService notifications = new NotificationService(
                new LegacyGatewayAdapter(
                        (destination, body) -> destination + "|" + body));
        String notification = notifications.notify(
                new ReleaseMessage("ops", "rel-42"));

        DeploymentCommandDispatcher commands = new DeploymentCommandDispatcher(Map.of(
                DeploymentAction.PAUSE, new PauseDeployment(),
                DeploymentAction.ROLLBACK, new RollbackDeployment()));

        var imported = new KeyValueReleaseImporter()
                .importRelease("id=rel-42;service=payments");

        var legacyGroup = new pl.training.module6.extractcomposite.before
                .LegacyPlanNodes.ReleaseGroup();
        legacyGroup.add(new pl.training.module6.extractcomposite.before
                .LegacyPlanNodes.TaskNode(13));
        var extractedGroup = new pl.training.module6.extractcomposite.after
                .PlanNodes.ReleaseGroup();
        extractedGroup.add(new pl.training.module6.extractcomposite.after
                .PlanNodes.TaskNode(13));

        System.out.println("Strategy cost: " + cost);
        System.out.println("Polymorphic steps: " + stepResults);
        System.out.println("Type object requires approval: " + request.requiresApproval());
        System.out.println("Composite total: " + plan.totalMinutes());
        System.out.println("Visitor total: " + plan.accept(new TotalMinutesVisitor()));
        System.out.println("Factory probe: " + probe);
        System.out.println("Decorated deployment: " + deployment + ", audit=" + audit);
        System.out.println("State: " + release.status());
        System.out.println("Observer events: " + events);
        System.out.println("Adapter result: " + notification);
        System.out.println("Command result: "
                + commands.dispatch(DeploymentAction.ROLLBACK, "rel-42"));
        System.out.println("Template result: " + imported);
        System.out.println("Singleton timeout: "
                + DeploymentDefaults.INSTANCE.healthCheckTimeout());
        System.out.println("Extract Composite equivalent: "
                + (legacyGroup.totalMinutes() == extractedGroup.totalMinutes()));
    }
}
