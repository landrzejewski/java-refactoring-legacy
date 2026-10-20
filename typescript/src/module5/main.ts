// Odpowiednik pl.training.module5.Module5Examples.
import { realpathSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { LegacyGatewayAdapter } from './adapter/after/LegacyGatewayAdapter.js';
import { NotificationService } from './adapter/after/NotificationService.js';
import { ReleaseMessage } from './adapter/after/ReleaseMessage.js';
import { LegacyCmdbRecord } from './anticorruptionlayer/LegacyCmdbRecord.js';
import { DeploymentPlanner } from './anticorruptionlayer/after/DeploymentPlanner.js';
import { CmdbEnvironmentDirectory } from './anticorruptionlayer/after/acl/CmdbEnvironmentDirectory.js';
import { CiBuildReportsAdapter } from './boundaryobject/after/CiBuildReportsAdapter.js';
import { ReleaseGate } from './boundaryobject/after/ReleaseGate.js';
import { ChangeRequest, Risk } from './chain/ChangeRequest.js';
import { ApprovalRouter } from './chain/after/ApprovalRouter.js';
import { DeploymentAction } from './command/after/DeploymentAction.js';
import { DeploymentCommandDispatcher } from './command/after/DeploymentCommandDispatcher.js';
import { PauseDeployment } from './command/after/PauseDeployment.js';
import { RollbackDeployment } from './command/after/RollbackDeployment.js';
import { PlanBuilder } from './composite/after/PlanBuilder.js';
import { TotalMinutesVisitor } from './composite/after/TotalMinutesVisitor.js';
import { AuditedDeploymentRunner } from './decorator/after/AuditedDeploymentRunner.js';
import { BasicDeploymentRunner } from './decorator/after/BasicDeploymentRunner.js';
import * as PlanNodes from './extractcomposite/after/PlanNodes.js';
import * as LegacyPlanNodes from './extractcomposite/before/LegacyPlanNodes.js';
import { ReleaseCli } from './facade/after/ReleaseCli.js';
import { ReleasePublishingFacade } from './facade/after/ReleasePublishingFacade.js';
import { DeploymentProbeFactory, ProbeKind } from './factory/after/DeploymentProbeFactory.js';
import { RolloutCompositionRoot } from './inlinesingleton/after/RolloutCompositionRoot.js';
import { ReleaseFormMediator } from './mediator/after/ReleaseFormMediator.js';
import { DeploymentStatus } from './middleman/DeploymentStatus.js';
import { DeploymentRegistry } from './middleman/after/DeploymentRegistry.js';
import { ReleaseDashboard } from './middleman/after/ReleaseDashboard.js';
import { ReleasePublished } from './observer/after/ReleasePublished.js';
import { ReleasePublisher } from './observer/after/ReleasePublisher.js';
import { ApprovalStep } from './polymorphism/after/ApprovalStep.js';
import { ScriptStep } from './polymorphism/after/ScriptStep.js';
import { DeploymentDefaults } from './singleton/DeploymentDefaults.js';
import { Release } from './state/after/Release.js';
import { DeploymentCostCalculator } from './strategy/after/DeploymentCostCalculator.js';
import { ExpeditedCostPolicy } from './strategy/after/ExpeditedCostPolicy.js';
import { formatList } from './support.js';
import { KeyValueReleaseImporter } from './templatemethod/after/KeyValueReleaseImporter.js';
import { ChangeEntry, Kind } from './templatetostrategy/ChangeEntry.js';
import { ContentSelection } from './templatetostrategy/after/ContentSelection.js';
import { MarkdownFormat } from './templatetostrategy/after/MarkdownFormat.js';
import { ReleaseNotesExporter } from './templatetostrategy/after/ReleaseNotesExporter.js';
import { DeploymentRequest } from './typecode/after/DeploymentRequest.js';
import { DeploymentZone } from './typecode/after/DeploymentZone.js';
import { CustomerTier } from './variability/CustomerTier.js';
import { ReleaseConsole } from './variability/after/ReleaseConsole.js';

export function main(): void {
  const cost = new DeploymentCostCalculator(new ExpeditedCostPolicy()).calculate(10_000);

  const stepResults = [new ScriptStep('deploy.sh').execute(), new ApprovalStep('anna').execute()];

  const request = new DeploymentRequest('rel-42', DeploymentZone.fromCode('prod'));

  const plan = PlanBuilder.group('release')
    .group('database', group => group.task('backup', 5).task('migrate', 8))
    .task('deploy', 3)
    .build();

  const probeFactory = new DeploymentProbeFactory();
  const probe = probeFactory.create(ProbeKind.HTTP, '/health').check();

  const audit: string[] = [];
  const deployment = new AuditedDeploymentRunner(
    new BasicDeploymentRunner(),
    entry => audit.push(entry),
  ).run('rel-42');

  const release = new Release();
  release.approve();
  release.deploy();

  const events: string[] = [];
  const publisher = new ReleasePublisher();
  publisher.subscribe(event => events.push('audit:' + event.releaseId));
  publisher.subscribe(event => events.push('metric:' + event.releaseId));
  publisher.publish(new ReleasePublished('rel-42'));

  const notifications = new NotificationService(
    new LegacyGatewayAdapter({ transmit: (destination, body) => destination + '|' + body }),
  );
  const notification = notifications.notify(new ReleaseMessage('ops', 'rel-42'));

  const commands = new DeploymentCommandDispatcher(
    new Map([
      [DeploymentAction.PAUSE, new PauseDeployment()],
      [DeploymentAction.ROLLBACK, new RollbackDeployment()],
    ]),
  );

  const imported = new KeyValueReleaseImporter().importRelease('id=rel-42;service=payments');

  const legacyGroup = new LegacyPlanNodes.ReleaseGroup();
  legacyGroup.add(new LegacyPlanNodes.TaskNode(13));
  const extractedGroup = new PlanNodes.ReleaseGroup();
  extractedGroup.add(new PlanNodes.TaskNode(13));

  console.log('Strategy cost: ' + cost);
  console.log('Polymorphic steps: ' + formatList(stepResults));
  console.log('Type object requires approval: ' + request.requiresApproval());
  console.log('Composite total: ' + plan.totalMinutes());
  console.log('Visitor total: ' + plan.accept(new TotalMinutesVisitor()));
  console.log('Factory probe: ' + probe);
  console.log('Decorated deployment: ' + deployment + ', audit=' + formatList(audit));
  console.log('State: ' + release.status());
  console.log('Observer events: ' + formatList(events));
  console.log('Adapter result: ' + notification);
  console.log('Command result: ' + commands.dispatch(DeploymentAction.ROLLBACK, 'rel-42'));
  console.log('Template result: ' + imported.toString());
  console.log('Singleton timeout: ' + DeploymentDefaults.INSTANCE.healthCheckTimeout().toString());
  console.log(
    'Extract Composite equivalent: ' + (legacyGroup.totalMinutes() === extractedGroup.totalMinutes()),
  );

  const registry = new DeploymentRegistry();
  registry.update('dep-42', DeploymentStatus.RUNNING);
  const dashboard = new ReleaseDashboard(registry);
  console.log('Remove Middle Man: ' + dashboard.render('dep-42'));

  const releaseConsole = ReleaseConsole.forTier(CustomerTier.PREMIUM);
  releaseConsole.submit('scale-up');
  releaseConsole.runQueued();
  releaseConsole.advance();
  console.log('Command/Strategy/State: cost=' + releaseConsole.supportCost(10)
    + ', log=' + formatList(releaseConsole.log()) + ', status=' + releaseConsole.status());

  const notes = new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.HIGHLIGHTS)
    .export('1.4.0', [
      new ChangeEntry(Kind.FEATURE, 'Blue-green deployments'),
      new ChangeEntry(Kind.FIX, 'Retry health check'),
    ]);
  console.log('Template Method -> Strategy: ' + notes.trim().replaceAll('\n', ' | '));

  const decision = ApprovalRouter.standard().route(new ChangeRequest('CR-7', 900, Risk.HIGH, true));
  console.log('Chain of Responsibility: ' + decision.approver + ' (' + decision.reason + ')');

  const gate = new ReleaseGate(new CiBuildReportsAdapter({
    fetchBuild: buildId => new Map([
      ['id', buildId],
      ['status', 'SUCCESS'],
      ['coverage', '82.5'],
      ['failed', '0'],
      ['commit', 'a1b2c3d'],
    ]),
  }));
  console.log('Boundary Object: ' + gate.summary('build-17') + ', releasable=' + gate.canRelease('build-17'));

  const effects: string[] = [];
  const published = new ReleaseCli(new ReleasePublishingFacade(effect => effects.push(effect)))
    .publish('rel-42', 'prod');
  console.log('Facade: ' + published + ', effects=' + formatList(effects));

  const planner = new DeploymentPlanner(new CmdbEnvironmentDirectory({
    fetchAll: () => [
      new LegacyCmdbRecord('prod-eu', 'P', 1, '20261015'),
      new LegacyCmdbRecord('test-a', 'T', 1, '20261019'),
      new LegacyCmdbRecord('dev-1', 'D', 0, '20260901'),
    ],
  }));
  console.log('Anti-Corruption Layer: canary targets=' + formatList(planner.canaryTargets()));

  const form = new ReleaseFormMediator();
  form.versionField().type('1.4.0');
  form.approverPicker().choose('anna');
  form.publishButton().click();
  console.log('Mediator: ' + form.snapshot().status + ', versionEditable=' + form.snapshot().versionEditable);

  console.log('Inline Singleton: ' + RolloutCompositionRoot.create('canary').plan('rel-42'));
}

// Uruchamiane tylko bezpośrednio (node dist/module5/main.js), nie przy imporcie w testach.
const entryPoint = process.argv[1];
if (entryPoint !== undefined && realpathSync(entryPoint) === fileURLToPath(import.meta.url)) {
  main();
}
