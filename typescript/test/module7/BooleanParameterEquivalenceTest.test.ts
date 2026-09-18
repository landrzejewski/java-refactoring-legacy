import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { DeploymentExecutor } from '../../src/module7/booleanparameter/after/DeploymentExecutor.js';
import { LegacyDeploymentExecutor } from '../../src/module7/booleanparameter/before/LegacyDeploymentExecutor.js';

const NULL = null as never;

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function messageOf(action: () => unknown): string {
  const failure = failureOf(action);
  expect(failure).toBeInstanceOf(IllegalArgumentError);
  return failure.message;
}

describe('BooleanParameterEquivalenceTest', () => {
  it('namedOperationsPreserveBothLegacyBranches', () => {
    const before = new LegacyDeploymentExecutor();
    const after = new DeploymentExecutor();

    expect(after.preview('dep-42')).toBe(before.execute('dep-42', true));
    expect(after.deploy('dep-42')).toBe(before.execute('dep-42', false));
  });

  it('validationIsIdenticalForBothNamedOperations', () => {
    const before = new LegacyDeploymentExecutor();
    const after = new DeploymentExecutor();

    for (const invalidId of [NULL as string, '', '  \t']) {
      const previewMessage = messageOf(() => before.execute(invalidId, true));
      const deployMessage = messageOf(() => before.execute(invalidId, false));

      expect(messageOf(() => after.preview(invalidId))).toBe(previewMessage);
      expect(messageOf(() => after.deploy(invalidId))).toBe(deployMessage);
    }
  });

  it('publicApiContainsNoBooleanParameterAndExecutorHasNoMutableState', () => {
    // Odpowiednik refleksji: publiczne metody na prototypie to wyłącznie
    // nazwane operacje, każda przyjmuje tylko identyfikator (bez flagi).
    const prototype = DeploymentExecutor.prototype as unknown as
      Record<string, (...args: unknown[]) => unknown>;
    const publicMethods = Object.getOwnPropertyNames(prototype)
      .filter(name => name !== 'constructor');

    expect(publicMethods.sort()).toEqual(['deploy', 'preview']);
    for (const name of publicMethods) {
      expect(prototype[name]?.length).toBe(1);
    }

    const executor = new DeploymentExecutor();
    expect(Object.keys(executor)).toEqual([]);
    expect(executor.preview('dep-42')).toBe('preview:dep-42');
    expect(executor.deploy('dep-42')).toBe('deployed:dep-42');
    expect(executor.preview('dep-42')).toBe('preview:dep-42');
  });
});
