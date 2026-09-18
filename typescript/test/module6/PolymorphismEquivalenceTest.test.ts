import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { ApprovalStep } from '../../src/module6/polymorphism/after/ApprovalStep.js';
import type { DeploymentStep } from '../../src/module6/polymorphism/after/DeploymentStep.js';
import { ScriptStep } from '../../src/module6/polymorphism/after/ScriptStep.js';
import { LegacyDeploymentStep } from '../../src/module6/polymorphism/before/LegacyDeploymentStep.js';

function failureMessage(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalArgumentError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalArgumentError');
}

describe('PolymorphismEquivalenceTest', () => {
  it('dispatchesEachStableVariantThroughItsOwnType', () => {
    const before = [
      LegacyDeploymentStep.script('deploy.sh').execute(),
      LegacyDeploymentStep.approval('anna').execute(),
    ];
    const steps: DeploymentStep[] = [new ScriptStep('deploy.sh'), new ApprovalStep('anna')];

    expect(steps.map(step => step.execute())).toEqual(before);
  });

  it('preservesInvalidValueContract', () => {
    expect(failureMessage(() => new ScriptStep(' '))).toBe(
      failureMessage(() => LegacyDeploymentStep.script(' ')),
    );
  });
});
