import { describe, expect, it } from 'vitest';
import { IllegalStateError } from '../../src/shared/errors.js';
import { AuditedDeploymentRunner } from '../../src/module6/decorator/after/AuditedDeploymentRunner.js';
import { BasicDeploymentRunner } from '../../src/module6/decorator/after/BasicDeploymentRunner.js';
import { LegacyDeploymentRunner } from '../../src/module6/decorator/before/LegacyDeploymentRunner.js';

function captureFailure(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('expected failure');
}

describe('DecoratorEquivalenceTest', () => {
  it('preservesResultAndAuditOrderOnSuccess', () => {
    const beforeAudit: string[] = [];
    const afterAudit: string[] = [];

    const before = new LegacyDeploymentRunner(entry => beforeAudit.push(entry)).run('rel-42');
    const after = new AuditedDeploymentRunner(new BasicDeploymentRunner(), entry =>
      afterAudit.push(entry),
    ).run('rel-42');

    expect(after).toBe(before);
    expect(afterAudit).toEqual(beforeAudit);
  });

  it('preservesAuditOrderAndPropagatesTheSameFailure', () => {
    const audit: string[] = [];
    const failure = new IllegalStateError('gateway unavailable');
    const runner = new AuditedDeploymentRunner(
      {
        run: () => {
          throw failure;
        },
      },
      entry => audit.push(entry),
    );

    const propagated = captureFailure(() => runner.run('rel-42'));

    expect(propagated).toBe(failure);
    // Java: getSimpleName() → "IllegalStateException"; w TS nazwa klasy błędu to "IllegalStateError".
    expect(audit).toEqual(['start:rel-42', 'failure:rel-42:IllegalStateError']);
  });

  it('invalidInputPreservesFailureAndAuditTrace', () => {
    const beforeAudit: string[] = [];
    const afterAudit: string[] = [];

    const beforeFailure = captureFailure(() =>
      new LegacyDeploymentRunner(entry => beforeAudit.push(entry)).run(' '),
    );
    const afterFailure = captureFailure(() =>
      new AuditedDeploymentRunner(new BasicDeploymentRunner(), entry => afterAudit.push(entry)).run(' '),
    );

    expect(afterFailure.constructor).toBe(beforeFailure.constructor);
    expect(afterFailure.message).toBe(beforeFailure.message);
    expect(afterAudit).toEqual(beforeAudit);
  });
});
