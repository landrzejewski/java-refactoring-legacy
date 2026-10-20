package pl.training.module5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.training.module5.adapter.after.LegacyGatewayAdapter;
import pl.training.module5.anticorruptionlayer.LegacyCmdbRecord;
import pl.training.module5.anticorruptionlayer.after.DeploymentPlanner;
import pl.training.module5.anticorruptionlayer.after.acl.CmdbEnvironmentDirectory;
import pl.training.module5.boundaryobject.after.CiBuildReportsAdapter;
import pl.training.module5.boundaryobject.after.ReleaseGate;
import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.after.ApprovalRouter;
import pl.training.module5.adapter.after.NotificationService;
import pl.training.module5.adapter.after.ReleaseMessage;
import pl.training.module5.command.after.DeploymentAction;
import pl.training.module5.command.after.DeploymentCommandDispatcher;
import pl.training.module5.command.after.PauseDeployment;
import pl.training.module5.command.after.RollbackDeployment;
import pl.training.module5.composite.after.DeploymentGroup;
import pl.training.module5.composite.after.PlanBuilder;
import pl.training.module5.composite.after.TotalMinutesVisitor;
import pl.training.module5.decorator.after.AuditedDeploymentRunner;
import pl.training.module5.decorator.after.BasicDeploymentRunner;
import pl.training.module5.facade.after.ReleaseCli;
import pl.training.module5.facade.after.ReleasePublishingFacade;
import pl.training.module5.factory.after.DeploymentProbeFactory;
import pl.training.module5.inlinesingleton.after.RolloutCompositionRoot;
import pl.training.module5.mediator.after.ReleaseFormMediator;
import pl.training.module5.middleman.DeploymentStatus;
import pl.training.module5.observer.after.ReleasePublished;
import pl.training.module5.observer.after.ReleasePublisher;
import pl.training.module5.polymorphism.after.ApprovalStep;
import pl.training.module5.polymorphism.after.ScriptStep;
import pl.training.module5.singleton.DeploymentDefaults;
import pl.training.module5.state.after.Release;
import pl.training.module5.strategy.after.DeploymentCostCalculator;
import pl.training.module5.strategy.after.ExpeditedCostPolicy;
import pl.training.module5.templatemethod.after.KeyValueReleaseImporter;
import pl.training.module5.templatetostrategy.ChangeEntry;
import pl.training.module5.templatetostrategy.after.ContentSelection;
import pl.training.module5.templatetostrategy.after.MarkdownFormat;
import pl.training.module5.templatetostrategy.after.ReleaseNotesExporter;
import pl.training.module5.typecode.after.DeploymentRequest;
import pl.training.module5.typecode.after.DeploymentZone;
import pl.training.module5.variability.CustomerTier;
import pl.training.module5.variability.after.ReleaseConsole;

public final class Module5Examples {
    private Module5Examples() {
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

        var legacyGroup = new pl.training.module5.extractcomposite.before
                .LegacyPlanNodes.ReleaseGroup();
        legacyGroup.add(new pl.training.module5.extractcomposite.before
                .LegacyPlanNodes.TaskNode(13));
        var extractedGroup = new pl.training.module5.extractcomposite.after
                .PlanNodes.ReleaseGroup();
        extractedGroup.add(new pl.training.module5.extractcomposite.after
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

        var registry = new pl.training.module5.middleman.after
                .DeploymentRegistry();
        registry.update("dep-42", DeploymentStatus.RUNNING);
        var dashboard = new pl.training.module5.middleman.after
                .ReleaseDashboard(registry);
        System.out.println("Remove Middle Man: " + dashboard.render("dep-42"));

        ReleaseConsole console = ReleaseConsole.forTier(CustomerTier.PREMIUM);
        console.submit("scale-up");
        console.runQueued();
        console.advance();
        System.out.println("Command/Strategy/State: cost=" + console.supportCost(10)
                + ", log=" + console.log() + ", status=" + console.status());

        String notes = new ReleaseNotesExporter(
                new MarkdownFormat(), ContentSelection.HIGHLIGHTS)
                .export("1.4.0", List.of(
                        new ChangeEntry(ChangeEntry.Kind.FEATURE, "Blue-green deployments"),
                        new ChangeEntry(ChangeEntry.Kind.FIX, "Retry health check")));
        System.out.println("Template Method -> Strategy: "
                + notes.strip().replace("\n", " | "));

        Decision decision = ApprovalRouter.standard().route(
                new ChangeRequest("CR-7", 900, ChangeRequest.Risk.HIGH, true));
        System.out.println("Chain of Responsibility: "
                + decision.approver() + " (" + decision.reason() + ")");

        ReleaseGate gate = new ReleaseGate(new CiBuildReportsAdapter(buildId -> Map.of(
                "id", buildId,
                "status", "SUCCESS",
                "coverage", "82.5",
                "failed", "0",
                "commit", "a1b2c3d")));
        System.out.println("Boundary Object: " + gate.summary("build-17")
                + ", releasable=" + gate.canRelease("build-17"));

        List<String> effects = new ArrayList<>();
        String published = new ReleaseCli(new ReleasePublishingFacade(effects::add))
                .publish("rel-42", "prod");
        System.out.println("Facade: " + published + ", effects=" + effects);

        DeploymentPlanner planner = new DeploymentPlanner(new CmdbEnvironmentDirectory(
                () -> List.of(
                        new LegacyCmdbRecord("prod-eu", "P", 1, "20261015"),
                        new LegacyCmdbRecord("test-a", "T", 1, "20261019"),
                        new LegacyCmdbRecord("dev-1", "D", 0, "20260901"))));
        System.out.println("Anti-Corruption Layer: canary targets="
                + planner.canaryTargets());

        ReleaseFormMediator form = new ReleaseFormMediator();
        form.versionField().type("1.4.0");
        form.approverPicker().choose("anna");
        form.publishButton().click();
        System.out.println("Mediator: " + form.snapshot().status()
                + ", versionEditable=" + form.snapshot().versionEditable());

        System.out.println("Inline Singleton: "
                + RolloutCompositionRoot.create("canary").plan("rel-42"));
    }
}
