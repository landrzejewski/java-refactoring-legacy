// Odpowiednik pl.training.module6.Module6Examples.
import { realpathSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { LegacyGatewayAdapter } from './adapter/after/LegacyGatewayAdapter.js';
import { NotificationService } from './adapter/after/NotificationService.js';
import { ReleaseMessage } from './adapter/after/ReleaseMessage.js';
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
import { DeploymentProbeFactory, ProbeKind } from './factory/after/DeploymentProbeFactory.js';
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
import { DeploymentRequest } from './typecode/after/DeploymentRequest.js';
import { DeploymentZone } from './typecode/after/DeploymentZone.js';

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
}

// Uruchamiane tylko bezpośrednio (node dist/module6/main.js), nie przy imporcie w testach.
const entryPoint = process.argv[1];
if (entryPoint !== undefined && realpathSync(entryPoint) === fileURLToPath(import.meta.url)) {
  main();
}
