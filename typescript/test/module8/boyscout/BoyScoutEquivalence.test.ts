import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../../src/shared/errors.js';
import { DeploymentResult } from '../../../src/module8/boyscout/DeploymentResult.js';
import { DeploymentStatus } from '../../../src/module8/boyscout/DeploymentStatus.js';
import { ReleaseSummaryFormatter as AfterFormatter } from '../../../src/module8/boyscout/after/ReleaseSummaryFormatter.js';
import { ReleaseSummaryFormatter as BeforeFormatter } from '../../../src/module8/boyscout/before/ReleaseSummaryFormatter.js';

type SummaryContract = (releaseId: string, results: readonly DeploymentResult[]) => string;

const NULL_STRING = null as unknown as string;
const NULL_RESULTS = null as unknown as readonly DeploymentResult[];
const NULL_RESULT = null as unknown as DeploymentResult;

function success(environment: string, description: string): DeploymentResult {
  return new DeploymentResult(DeploymentStatus.SUCCESS, environment, description);
}

function failure(environment: string, description: string): DeploymentResult {
  return new DeploymentResult(DeploymentStatus.FAILURE, environment, description);
}

function thrownBy(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(Error);
    return error as Error;
  }
  throw new Error('expected an exception');
}

function assertSameFailure(beforeAction: () => unknown, afterAction: () => unknown): void {
  const beforeFailure = thrownBy(beforeAction);
  const afterFailure = thrownBy(afterAction);

  expect(afterFailure.constructor).toBe(beforeFailure.constructor);
  expect(afterFailure.message).toBe(beforeFailure.message);
}

function assertFailure(
  expectedType: new (...args: never[]) => Error,
  expectedMessage: string,
  action: () => unknown,
): void {
  const error = thrownBy(action);
  expect(error).toBeInstanceOf(expectedType);
  expect(error.message).toBe(expectedMessage);
}

describe('BoyScoutEquivalenceTest', () => {
  const before = new BeforeFormatter();
  const after = new AfterFormatter();

  const representativeResults: [string, string, readonly DeploymentResult[]][] = [
    ['single success', 'release-1', [success('test', 'deployed')]],
    ['single failure', 'release-2', [failure('production', 'timeout')]],
    [
      'mixed result in stable order',
      ' release-3 ',
      [
        success('test', 'deployed'),
        failure('staging', 'health check failed'),
        success('production', 'deployed'),
      ],
    ],
  ];

  it.each(representativeResults)(
    'localCleanupPreservesRepresentativeOutputs: %s',
    (_description, releaseId, results) => {
      expect(after.format(releaseId, results)).toBe(before.format(releaseId, results));
    },
  );

  it('refactoredCodeProducesTheIndependentlySpecifiedSummary', () => {
    const results = [success('test', 'deployed'), failure('production', 'timeout')];

    expect(after.format(' release-42 ', results)).toBe(
      'Release release-42\n' +
        '[OK] test: deployed\n' +
        '[ERROR] production: timeout\n' +
        'Successful: 1/2',
    );
  });

  it('localCleanupPreservesValidationFailuresAndTheirOrder', () => {
    assertSameFailure(
      () => before.format(NULL_STRING, [success('test', 'ok')]),
      () => after.format(NULL_STRING, [success('test', 'ok')]),
    );
    assertSameFailure(
      () => before.format('release-1', NULL_RESULTS),
      () => after.format('release-1', NULL_RESULTS),
    );
    assertSameFailure(
      () => before.format('   ', [success('test', 'ok')]),
      () => after.format('   ', [success('test', 'ok')]),
    );
    assertSameFailure(
      () => before.format('release-1', []),
      () => after.format('release-1', []),
    );
    assertSameFailure(
      () => before.format('release-1', [NULL_RESULT]),
      () => after.format('release-1', [NULL_RESULT]),
    );
    assertSameFailure(
      () => before.format(' ', NULL_RESULTS),
      () => after.format(' ', NULL_RESULTS),
    );
  });

  it('bothVersionsExposeTheSameCallableContract', () => {
    const oldContract: SummaryContract = before.format.bind(before);
    const cleanedContract: SummaryContract = after.format.bind(after);
    const results = [success('test', 'ok')];

    expect(cleanedContract('release-1', results)).toBe(oldContract('release-1', results));
  });

  it('sharedInputModelRejectsIncompleteResults', () => {
    assertFailure(
      NullPointerError,
      'status',
      () => new DeploymentResult(null as unknown as DeploymentStatus, 'test', 'ok'),
    );
    assertFailure(
      NullPointerError,
      'environment',
      () => new DeploymentResult(DeploymentStatus.SUCCESS, NULL_STRING, 'ok'),
    );
    assertFailure(
      IllegalArgumentError,
      'environment must not be blank',
      () => new DeploymentResult(DeploymentStatus.SUCCESS, ' ', 'ok'),
    );
    assertFailure(
      NullPointerError,
      'description',
      () => new DeploymentResult(DeploymentStatus.SUCCESS, 'test', NULL_STRING),
    );
    assertFailure(
      IllegalArgumentError,
      'description must not be blank',
      () => new DeploymentResult(DeploymentStatus.SUCCESS, 'test', ' '),
    );
  });
});
