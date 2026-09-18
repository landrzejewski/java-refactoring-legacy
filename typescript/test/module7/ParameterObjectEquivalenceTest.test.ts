import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../src/shared/errors.js';
import { RolloutPlanner } from '../../src/module7/parameterobject/after/RolloutPlanner.js';
import { RolloutSpec } from '../../src/module7/parameterobject/after/RolloutSpec.js';
import { LegacyRolloutPlanner } from '../../src/module7/parameterobject/before/LegacyRolloutPlanner.js';

const NULL = null as never;
const INTEGER_MAX_VALUE = 2_147_483_647;

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function assertSameFailure(
  legacyCall: () => unknown,
  parameterObjectConstruction: () => unknown,
): void {
  const legacyFailure = failureOf(legacyCall);
  const constructionFailure = failureOf(parameterObjectConstruction);

  expect(constructionFailure.constructor).toBe(legacyFailure.constructor);
  expect(constructionFailure.message).toBe(legacyFailure.message);
}

describe('ParameterObjectEquivalenceTest', () => {
  it('preservesEstimationAndDescription', () => {
    const legacy = new LegacyRolloutPlanner();
    const refactored = new RolloutPlanner();
    const spec = new RolloutSpec('payments', 'eu-central-1', 10, 3, 15);

    expect(legacy.estimateSeconds('payments', 'eu-central-1', 10, 3, 15)).toBe(165);
    expect(refactored.estimateSeconds(spec))
      .toBe(legacy.estimateSeconds('payments', 'eu-central-1', 10, 3, 15));

    const expected = 'service=payments;region=eu-central-1;instances=10;'
      + 'batchSize=3;pauseSeconds=15';
    expect(legacy.describe('payments', 'eu-central-1', 10, 3, 15)).toBe(expected);
    expect(refactored.describe(spec)).toBe(expected);
  });

  it('usesOverflowSafeCeilingDivision', () => {
    const legacy = new LegacyRolloutPlanner();
    const refactored = new RolloutPlanner();
    const spec = new RolloutSpec(
      'search',
      'eu-west-1',
      INTEGER_MAX_VALUE,
      2,
      0);

    const expected = 32_212_254_720;
    expect(legacy.estimateSeconds(
      'search',
      'eu-west-1',
      INTEGER_MAX_VALUE,
      2,
      0)).toBe(expected);
    expect(refactored.estimateSeconds(spec)).toBe(expected);
  });

  it('movesValidationFromEveryOperationToParameterObjectConstruction', () => {
    const legacy = new LegacyRolloutPlanner();

    const legacyFailure = failureOf(
      () => legacy.describe('payments', 'eu-central-1', 0, 0, -1));
    const constructionFailure = failureOf(
      () => new RolloutSpec('payments', 'eu-central-1', 0, 0, -1));

    expect(legacyFailure).toBeInstanceOf(IllegalArgumentError);
    expect(constructionFailure).toBeInstanceOf(IllegalArgumentError);
    expect(legacyFailure.message).toBe('instances must be greater than zero');
    expect(constructionFailure.message).toBe(legacyFailure.message);
  });

  it('preservesValidationTypeMessageAndOrder', () => {
    const legacy = new LegacyRolloutPlanner();

    assertSameFailure(
      () => legacy.describe(NULL, NULL, 0, 0, -1),
      () => new RolloutSpec(NULL, NULL, 0, 0, -1));
    assertSameFailure(
      () => legacy.describe(' ', NULL, 0, 0, -1),
      () => new RolloutSpec(' ', NULL, 0, 0, -1));
    assertSameFailure(
      () => legacy.describe('api', NULL, 0, 0, -1),
      () => new RolloutSpec('api', NULL, 0, 0, -1));
    assertSameFailure(
      () => legacy.describe('api', ' ', 0, 0, -1),
      () => new RolloutSpec('api', ' ', 0, 0, -1));
    assertSameFailure(
      () => legacy.describe('api', 'eu', 0, 0, -1),
      () => new RolloutSpec('api', 'eu', 0, 0, -1));
    assertSameFailure(
      () => legacy.describe('api', 'eu', 1, 0, -1),
      () => new RolloutSpec('api', 'eu', 1, 0, -1));
    assertSameFailure(
      () => legacy.describe('api', 'eu', 1, 1, -1),
      () => new RolloutSpec('api', 'eu', 1, 1, -1));
  });

  it('rejectsNullParameterObjectAtTheNewApiBoundary', () => {
    const planner = new RolloutPlanner();

    const estimateFailure = failureOf(() => planner.estimateSeconds(NULL));
    const describeFailure = failureOf(() => planner.describe(NULL));

    expect(estimateFailure).toBeInstanceOf(NullPointerError);
    expect(describeFailure).toBeInstanceOf(NullPointerError);
    expect(estimateFailure.message).toBe('spec');
    expect(describeFailure.message).toBe('spec');
  });
});
