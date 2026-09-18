import { describe, expect, it } from 'vitest';
import { DeploymentDefaults } from '../../src/module6/singleton/DeploymentDefaults.js';
import { LegacyDeploymentDefaults } from '../../src/module6/singleton/before/LegacyDeploymentDefaults.js';
import { Duration } from '../../src/module6/support.js';

describe('SingletonContractTest', () => {
  it('enumLimitsInstantiationWithoutChangingTheDefaultValue', () => {
    const firstLegacy = new LegacyDeploymentDefaults();
    const secondLegacy = new LegacyDeploymentDefaults();

    expect(firstLegacy).not.toBe(secondLegacy);
    expect(DeploymentDefaults.valueOf('INSTANCE')).toBe(DeploymentDefaults.INSTANCE);
    expect(DeploymentDefaults.INSTANCE.healthCheckTimeout()).toEqual(firstLegacy.healthCheckTimeout());
    expect(firstLegacy.healthCheckTimeout()).toEqual(Duration.ofSeconds(30));
  });

  it('allParallelAccessesObserveTheSameEnumConstant', async () => {
    // JS jest jednowątkowy — „równoległość” symulujemy 1000 współbieżnymi zadaniami asynchronicznymi.
    const instances = await Promise.all(
      Array.from({ length: 1_000 }, async () => {
        await Promise.resolve();
        return DeploymentDefaults.INSTANCE;
      }),
    );

    expect(new Set(instances).size).toBe(1);
  });
});
