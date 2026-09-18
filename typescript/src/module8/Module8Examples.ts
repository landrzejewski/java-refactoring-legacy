import { Decimal } from 'decimal.js';
import { DeploymentResult } from './boyscout/DeploymentResult.js';
import { DeploymentStatus } from './boyscout/DeploymentStatus.js';
import { ReleaseSummaryFormatter } from './boyscout/after/ReleaseSummaryFormatter.js';
import { ChangeIntent } from './collaboration/ChangeIntent.js';
import { ChangeSet } from './collaboration/ChangeSet.js';
import { EvidenceKind } from './collaboration/EvidenceKind.js';
import { ExampleTeamReviewPolicy } from './collaboration/ExampleTeamReviewPolicy.js';
import { VerificationEvidence } from './collaboration/VerificationEvidence.js';
import { ConsequenceKind } from './documentation/ConsequenceKind.js';
import { DecisionConsequence } from './documentation/DecisionConsequence.js';
import { DecisionId } from './documentation/DecisionId.js';
import { DecisionOption } from './documentation/DecisionOption.js';
import { DecisionRecord } from './documentation/DecisionRecord.js';
import { DecisionRecordMarkdownRenderer } from './documentation/DecisionRecordMarkdownRenderer.js';
import { DecisionStatus } from './documentation/DecisionStatus.js';
import { CandidatePricingEngine } from './incremental/CandidatePricingEngine.js';
import { LegacyPricingEngineAdapter } from './incremental/LegacyPricingEngineAdapter.js';
import { MigratingPricingEngine } from './incremental/MigratingPricingEngine.js';
import { MigrationMode } from './incremental/MigrationMode.js';
import { PriceRequest } from './incremental/PriceRequest.js';
import type { VerificationEvent } from './incremental/VerificationEvent.js';
import { RolloutPolicy } from './risk/RolloutPolicy.js';
import { RolloutSnapshot } from './risk/RolloutSnapshot.js';
import { RolloutThresholds } from './risk/RolloutThresholds.js';
import { InMemoryTypeScriptCompiler } from './tooling/InMemoryTypeScriptCompiler.js';
import { WarningPolicy } from './tooling/WarningPolicy.js';

export class Module8Examples {
  private constructor() {}

  static runExamples(): readonly string[] {
    return Object.freeze([
      Module8Examples.incrementalMigrationExample(),
      Module8Examples.boyScoutExample(),
      Module8Examples.collaborationExample(),
      Module8Examples.documentationExample(),
      Module8Examples.toolingExample(),
      Module8Examples.riskExample(),
    ]);
  }

  private static incrementalMigrationExample(): string {
    const events: VerificationEvent[] = [];
    const engine = new MigratingPricingEngine(
      new LegacyPricingEngineAdapter(),
      new CandidatePricingEngine(),
      { report: (event) => events.push(event) },
      MigrationMode.VERIFY,
    );

    const quote = engine.quote(new PriceRequest(new Decimal('19.99'), 3, new Decimal('0.10')));

    const event = events[0]?.kind;
    return `Stopniowa migracja: ${quote.netAmount.toFixed(2)}/${event}`;
  }

  private static boyScoutExample(): string {
    const formatter = new ReleaseSummaryFormatter();
    const summary = formatter.format('release-42', [
      new DeploymentResult(DeploymentStatus.SUCCESS, 'test', 'deployed'),
      new DeploymentResult(DeploymentStatus.FAILURE, 'prod', 'timeout'),
    ]);

    return `Boy Scout: ${summary.split('\n').at(-1)}`;
  }

  private static collaborationExample(): string {
    const changeSet = new ChangeSet(
      'Extract deployment clock',
      [ChangeIntent.REFACTORING],
      [new VerificationEvidence(EvidenceKind.AUTOMATED_TEST, 'Characterization suite passed')],
      true,
    );

    const ready = new ExampleTeamReviewPolicy().assess(changeSet).ready();
    return `Code review: ready=${ready}`;
  }

  private static documentationExample(): string {
    const record = new DecisionRecord(
      new DecisionId('ADR-0042'),
      'Use Branch by Abstraction',
      DecisionStatus.ACCEPTED,
      'The pricing engine must be replaced incrementally.',
      'Route both implementations through one client contract.',
      [
        new DecisionOption('Branch by Abstraction', 'Supports incremental verification.'),
        new DecisionOption('Big bang', 'Removes coexistence but delays feedback.'),
      ],
      [
        new DecisionConsequence(ConsequenceKind.POSITIVE, 'Rollout can stop after each stage.'),
        new DecisionConsequence(
          ConsequenceKind.NEGATIVE,
          'Two implementations coexist temporarily.',
        ),
      ],
      'Compare results in VERIFY mode before switching traffic.',
    );

    const heading = new DecisionRecordMarkdownRenderer().render(record).split('\n')[0];
    return `Dokumentowanie: ${heading}`;
  }

  private static toolingExample(): string {
    const source = `export class CleanSample {
    doubleValue(value: number): number {
        return value * 2;
    }
}
`;
    const successful = new InMemoryTypeScriptCompiler().compile(
      'example.CleanSample',
      source,
      WarningPolicy.TREAT_WARNINGS_AS_ERRORS,
    ).successful;

    return `Narzędzia: compiled=${successful}`;
  }

  private static riskExample(): string {
    const policy = new RolloutPolicy(new RolloutThresholds(100, 0.05, 250.0));
    const decision = policy.decide(new RolloutSnapshot(200, 4, 0, 180.0));
    return `Zarządzanie ryzykiem: ${decision}`;
  }

  static main(_args: readonly string[] = []): void {
    Module8Examples.runExamples().forEach((line) => console.log(line));
  }
}
