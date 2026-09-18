import { DeploymentCandidate } from './arrowhead/DeploymentCandidate.js';
import { DeploymentEligibility } from './arrowhead/after/DeploymentEligibility.js';
import { DeploymentExecutor } from './booleanparameter/after/DeploymentExecutor.js';
import { DeploymentWindowService } from './breakdependencies/after/DeploymentWindowService.js';
import { StandardMaintenanceWindows } from './breakdependencies/after/StandardMaintenanceWindows.js';
import { ManifestEntry } from './breakmethod/ManifestEntry.js';
import { ReleaseManifestBuilder } from './breakmethod/after/ReleaseManifestBuilder.js';
import { DeploymentSample } from './breakresponsibilities/DeploymentSample.js';
import { DeploymentMetricsCalculator } from './breakresponsibilities/after/DeploymentMetricsCalculator.js';
import { DeploymentReportFormatter } from './breakresponsibilities/after/DeploymentReportFormatter.js';
import { DeploymentReportService } from './breakresponsibilities/after/DeploymentReportService.js';
import { DeploymentCapacity } from './contract/after/DeploymentCapacity.js';
import { ReleaseGate } from './doublenegative/after/ReleaseGate.js';
import { ReleaseReadiness } from './doublenegative/after/ReleaseReadiness.js';
import { ArtifactPublisher } from './duplication/after/ArtifactPublisher.js';
import { AuditTrail } from './godclass/after/AuditTrail.js';
import { InMemoryReleaseRepository } from './godclass/after/InMemoryReleaseRepository.js';
import { ReleaseApplicationService } from './godclass/after/ReleaseApplicationService.js';
import { ReleaseNotifier } from './godclass/after/ReleaseNotifier.js';
import { ReleaseValidator } from './godclass/after/ReleaseValidator.js';
import { DeploymentRiskInput } from './methodobject/DeploymentRiskInput.js';
import { DeploymentRiskCalculator } from './methodobject/after/DeploymentRiskCalculator.js';
import { DeploymentStatus } from './middleman/DeploymentStatus.js';
import { DeploymentRegistry } from './middleman/after/DeploymentRegistry.js';
import { ReleaseDashboard } from './middleman/after/ReleaseDashboard.js';
import { RolloutPlanner } from './parameterobject/after/RolloutPlanner.js';
import { RolloutSpec } from './parameterobject/after/RolloutSpec.js';
import { Artifact } from './returnasap/Artifact.js';
import { ArtifactFinder } from './returnasap/after/ArtifactFinder.js';

export function runExamples(): readonly string[] {
  const results: string[] = [];

  const windowService = new DeploymentWindowService(
    new StandardMaintenanceWindows());
  results.push('Break Dependencies: '
    + windowService.schedule('payments', 2));

  const risk = new DeploymentRiskCalculator().calculate(
    new DeploymentRiskInput(10, 2, 1, true));
  results.push('Extract Method Object: '
    + risk.score + '/' + risk.level);

  const reportService = new DeploymentReportService(
    new DeploymentMetricsCalculator(),
    new DeploymentReportFormatter());
  results.push('Break Responsibilities: ' + reportService.generate([
    new DeploymentSample(12, true),
    new DeploymentSample(18, false),
    new DeploymentSample(24, true)]));

  results.push('Remove Duplication: '
    + new ArtifactPublisher().publishRelease(' IMAGE ', 42));

  const manifest = new ReleaseManifestBuilder().build([
    new ManifestEntry('worker', 'sha-worker', 20),
    new ManifestEntry('api', 'sha-api', 10)]);
  results.push('Break Method: ' + manifest.replaceAll('\n', ','));

  const rolloutSeconds = new RolloutPlanner().estimateSeconds(
    new RolloutSpec('payments', 'eu-central-1', 10, 3, 15));
  results.push('Introduce Parameter Object: ' + rolloutSeconds + 's');

  results.push('Remove Arrowhead: '
    + new DeploymentEligibility().evaluate(
      new DeploymentCandidate('rel-42', true, true, true)));

  const capacity = new DeploymentCapacity(10);
  capacity.reserve(3);
  results.push('Design by Contract: remaining=' + capacity.remaining());

  const canRelease = new ReleaseGate().canRelease(
    new ReleaseReadiness(true, true, true));
  results.push('Remove Double Negative: ' + canRelease);

  const repository = new InMemoryReleaseRepository();
  const auditTrail = new AuditTrail();
  const notifier = new ReleaseNotifier();
  const releaseService = new ReleaseApplicationService(
    new ReleaseValidator(), repository, auditTrail, notifier);
  const release = releaseService.publish('rel-42', 'payments', '2.1.0');
  results.push('Remove God Class: ' + release.releaseId
    + '/' + repository.findAll().length
    + '/' + auditTrail.entries().length
    + '/' + notifier.notifications().length);

  results.push('Remove Boolean Parameter: '
    + new DeploymentExecutor().deploy('rel-42'));

  const registry = new DeploymentRegistry();
  registry.update('dep-42', DeploymentStatus.RUNNING);
  const dashboard = new ReleaseDashboard(registry);
  results.push('Remove Middle Man: ' + dashboard.render('dep-42'));

  const artifact = new ArtifactFinder().findByChecksum(
    [
      new Artifact('api.jar', 'sha-1'),
      new Artifact('worker.jar', 'sha-2')],
    'sha-1');
  if (artifact === undefined) {
    // Optional.orElseThrow() → NoSuchElementException
    throw new Error('No value present');
  }
  results.push('Return ASAP: ' + artifact.name);

  // List.copyOf → niemodyfikowalna kopia
  return Object.freeze([...results]);
}

export function main(): void {
  runExamples().forEach(line => console.log(line));
}
