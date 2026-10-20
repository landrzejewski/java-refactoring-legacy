import { Decimal } from 'decimal.js';
import { ConsoleQuoteNotifier } from './adapter/ConsoleQuoteNotifier.js';
import { InMemoryQuoteRepository } from './adapter/InMemoryQuoteRepository.js';
import { Command, CreateDeliveryQuote } from './application/CreateDeliveryQuote.js';
import { DeploymentResult } from './boyscout/DeploymentResult.js';
import { DeploymentStatus } from './boyscout/DeploymentStatus.js';
import { ReleaseSummaryFormatter } from './boyscout/after/ReleaseSummaryFormatter.js';
import { ConsequenceKind } from './documentation/ConsequenceKind.js';
import { DecisionConsequence } from './documentation/DecisionConsequence.js';
import { DecisionId } from './documentation/DecisionId.js';
import { DecisionOption } from './documentation/DecisionOption.js';
import { DecisionRecord } from './documentation/DecisionRecord.js';
import { DecisionRecordMarkdownRenderer } from './documentation/DecisionRecordMarkdownRenderer.js';
import { DecisionStatus } from './documentation/DecisionStatus.js';
import { DeliveryPriceCalculator } from './domain/DeliveryPriceCalculator.js';
import { ExpressDeliveryPricePolicy } from './domain/ExpressDeliveryPricePolicy.js';
import { FuelSurcharge } from './domain/FuelSurcharge.js';
import { Parcel } from './domain/Parcel.js';
import { ShippingMethod } from './domain/ShippingMethod.js';
import { StandardDeliveryPricePolicy } from './domain/StandardDeliveryPricePolicy.js';
import { LegacyDeliveryQuoteService } from './legacy/LegacyDeliveryQuoteService.js';
import { InMemoryTypeScriptCompiler } from './tooling/InMemoryTypeScriptCompiler.js';
import { WarningPolicy } from './tooling/WarningPolicy.js';

export class Module2Examples {
  private constructor() {}

  static main(_args: readonly string[] = []): void {
    const parcel = new Parcel(new Decimal('3.00'));
    const legacy = new LegacyDeliveryQuoteService();
    const legacyQuote = legacy.createQuote(
      'developer@example.com',
      ShippingMethod.STANDARD,
      parcel);

    const fuelSurcharge = new FuelSurcharge(new Decimal('0.08'));
    const calculator = new DeliveryPriceCalculator([
      new StandardDeliveryPricePolicy(fuelSurcharge),
      new ExpressDeliveryPricePolicy(fuelSurcharge),
    ]);
    const repository = new InMemoryQuoteRepository();
    const useCase = new CreateDeliveryQuote(
      calculator,
      repository,
      new ConsoleQuoteNotifier());

    const refactoredQuote = useCase.execute(new Command(
      'developer@example.com',
      ShippingMethod.STANDARD,
      parcel));

    console.log(
      'Legacy and refactored prices equal: '
        + legacyQuote.price.equals(refactoredQuote.price));
    console.log('Stored quotes: ' + repository.quotes().length);

    Module2Examples.runBoyScoutExample();
    Module2Examples.runDecisionRecordExample();
    Module2Examples.runCompilerGateExample();
  }

  private static runBoyScoutExample(): void {
    const formatter = new ReleaseSummaryFormatter();
    const summary = formatter.format('release-42', [
      new DeploymentResult(DeploymentStatus.SUCCESS, 'test', 'deployed'),
      new DeploymentResult(DeploymentStatus.FAILURE, 'prod', 'timeout'),
    ]);

    console.log('Boy Scout: ' + summary.split('\n').at(-1));
  }

  private static runDecisionRecordExample(): void {
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
          'Two implementations coexist temporarily.'),
      ],
      'Compare results in VERIFY mode before switching traffic.');

    console.log('Decision record: '
      + new DecisionRecordMarkdownRenderer().render(record).split('\n')[0]);
  }

  private static runCompilerGateExample(): void {
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

    console.log('Compiler gate: compiled=' + successful);
  }
}
