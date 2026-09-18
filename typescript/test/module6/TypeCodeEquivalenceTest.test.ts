import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { DeploymentRequest } from '../../src/module6/typecode/after/DeploymentRequest.js';
import { DeploymentZone } from '../../src/module6/typecode/after/DeploymentZone.js';
import { LegacyDeploymentRequest } from '../../src/module6/typecode/before/LegacyDeploymentRequest.js';

function failureMessage(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalArgumentError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalArgumentError');
}

// Java przekazuje null — w TS wymaga to jawnego obejścia typów.
const missingCode = null as unknown as string;

describe('TypeCodeEquivalenceTest', () => {
  it('preservesMeaningOfEveryKnownCode', () => {
    for (const code of ['TEST', 'PROD', 'DR', 'prod']) {
      const before = new LegacyDeploymentRequest('rel-42', code);
      const after = new DeploymentRequest('rel-42', DeploymentZone.fromCode(code));

      expect(after.requiresApproval()).toBe(before.requiresApproval());
      expect(after.zone.code).toBe(before.zoneCode);
    }
  });

  it('canonicalizesKnownInstancesAndRejectsUnknownCodes', () => {
    expect(DeploymentZone.fromCode('prod')).toBe(DeploymentZone.PRODUCTION);
    expect(failureMessage(() => DeploymentZone.fromCode(missingCode))).toBe(
      failureMessage(() => new LegacyDeploymentRequest('rel-42', missingCode)),
    );
    expect(failureMessage(() => DeploymentZone.fromCode('unknown'))).toBe(
      failureMessage(() => new LegacyDeploymentRequest('rel-42', 'unknown')),
    );
  });
});
