import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../../src/shared/errors.js';
import { ConsequenceKind } from '../../../src/module8/documentation/ConsequenceKind.js';
import { DecisionConsequence } from '../../../src/module8/documentation/DecisionConsequence.js';
import { DecisionId } from '../../../src/module8/documentation/DecisionId.js';
import { DecisionOption } from '../../../src/module8/documentation/DecisionOption.js';
import { DecisionRecord } from '../../../src/module8/documentation/DecisionRecord.js';
import { DecisionStatus } from '../../../src/module8/documentation/DecisionStatus.js';

function record(
  options: readonly DecisionOption[],
  consequences: readonly DecisionConsequence[],
): DecisionRecord {
  return new DecisionRecord(
    new DecisionId('ADR-0042'),
    'Use a clock port',
    DecisionStatus.ACCEPTED,
    'The domain reads system time directly.',
    'Introduce a clock port at the application boundary.',
    options,
    consequences,
    'Run characterization tests before and after the change.',
  );
}

describe('DecisionRecordTest', () => {
  it('snapshotsCollectionsAndExposesImmutableViews', () => {
    const options = [new DecisionOption('Port', 'Separates the domain from time access')];
    const consequences = [
      new DecisionConsequence(ConsequenceKind.POSITIVE, 'Tests can supply a deterministic clock'),
    ];

    const decisionRecord = record(options, consequences);
    options.length = 0;
    consequences.length = 0;

    expect(decisionRecord.consideredOptions.length).toBe(1);
    expect(decisionRecord.consequences.length).toBe(1);
    expect(() => (decisionRecord.consideredOptions as DecisionOption[]).splice(0)).toThrow(
      TypeError,
    );
    expect(() => (decisionRecord.consequences as DecisionConsequence[]).splice(0)).toThrow(
      TypeError,
    );
  });

  it('rejectsInvalidIdentifiersAndIncompleteRecords', () => {
    expect(() => new DecisionId(null as unknown as string)).toThrow(NullPointerError);
    expect(() => new DecisionId('42')).toThrow(IllegalArgumentError);

    expect(
      () =>
        new DecisionRecord(
          new DecisionId('ADR-0042'),
          ' ',
          DecisionStatus.PROPOSED,
          'Context',
          'Decision',
          [new DecisionOption('Option', 'Rationale')],
          [new DecisionConsequence(ConsequenceKind.NEUTRAL, 'Consequence')],
          'Run tests',
        ),
    ).toThrow(IllegalArgumentError);
    expect(() =>
      record([], [new DecisionConsequence(ConsequenceKind.NEUTRAL, 'Consequence')]),
    ).toThrow(IllegalArgumentError);
    expect(() => record([new DecisionOption('Option', 'Rationale')], [])).toThrow(
      IllegalArgumentError,
    );
    expect(() => new DecisionOption('Option', ' ')).toThrow(IllegalArgumentError);
    expect(() => new DecisionConsequence(ConsequenceKind.POSITIVE, ' ')).toThrow(
      IllegalArgumentError,
    );
  });
});
