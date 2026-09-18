import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, IllegalStateError } from '../../src/shared/errors.js';
import { Contracts } from '../../src/module7/contract/after/Contracts.js';
import { DeploymentCapacity } from '../../src/module7/contract/after/DeploymentCapacity.js';
import { LegacyDeploymentCapacity } from '../../src/module7/contract/before/LegacyDeploymentCapacity.js';

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function messageOf(errorType: new (message?: string) => Error, action: () => unknown): string {
  const failure = failureOf(action);
  expect(failure).toBeInstanceOf(errorType);
  return failure.message;
}

function assertSameRemaining(
  before: LegacyDeploymentCapacity,
  after: DeploymentCapacity,
  expected: number,
): void {
  expect(before.remaining()).toBe(expected);
  expect(after.remaining()).toBe(expected);
  expect(after.remaining()).toBe(before.remaining());
}

function assertFailureWithoutMutation(
  capacity: DeploymentCapacity,
  operation: () => void,
  expectedMessage: string,
  expectedRemaining: number,
): void {
  const before = capacity.remaining();
  expect(before).toBe(expectedRemaining);
  expect(messageOf(IllegalArgumentError, operation)).toBe(expectedMessage);
  expect(capacity.remaining()).toBe(before);
}

describe('DesignByContractTest', () => {
  it('explicitContractsPreserveEveryValidStateTransition', () => {
    const before = new LegacyDeploymentCapacity(10);
    const after = new DeploymentCapacity(10);

    assertSameRemaining(before, after, 10);
    before.reserve(3);
    after.reserve(3);
    assertSameRemaining(before, after, 7);
    before.reserve(7);
    after.reserve(7);
    assertSameRemaining(before, after, 0);
    before.release(4);
    after.release(4);
    assertSameRemaining(before, after, 4);
    before.release(6);
    after.release(6);
    assertSameRemaining(before, after, 10);

    expect(new LegacyDeploymentCapacity(0).remaining()).toBe(0);
    expect(new DeploymentCapacity(0).remaining()).toBe(0);
  });

  it('preconditionsRejectInvalidOperationsBeforeMutation', () => {
    expect(messageOf(IllegalArgumentError, () => new DeploymentCapacity(-1)))
      .toBe('totalSlots must not be negative');

    const capacity = new DeploymentCapacity(5);
    assertFailureWithoutMutation(
      capacity, () => capacity.reserve(0), 'slots must be positive', 5);
    assertFailureWithoutMutation(
      capacity, () => capacity.reserve(-1), 'slots must be positive', 5);
    assertFailureWithoutMutation(
      capacity, () => capacity.reserve(6),
      'cannot reserve more slots than remain', 5);

    capacity.reserve(3);
    assertFailureWithoutMutation(
      capacity, () => capacity.release(0), 'slots must be positive', 2);
    assertFailureWithoutMutation(
      capacity, () => capacity.release(-1), 'slots must be positive', 2);
    assertFailureWithoutMutation(
      capacity, () => capacity.release(4),
      'cannot release more slots than are reserved', 2);
  });

  it('contractHelpersUseRuntimeExceptionsWithoutJavaAssertions', () => {
    expect(messageOf(IllegalArgumentError,
      () => Contracts.require(false, 'precondition'))).toBe('precondition');
    expect(messageOf(IllegalStateError,
      () => Contracts.ensure(false, 'postcondition'))).toBe('postcondition');
    expect(messageOf(IllegalStateError,
      () => Contracts.invariant(false, 'invariant'))).toBe('invariant');

    Contracts.require(true, 'ignored');
    Contracts.ensure(true, 'ignored');
    Contracts.invariant(true, 'ignored');
  });
});
