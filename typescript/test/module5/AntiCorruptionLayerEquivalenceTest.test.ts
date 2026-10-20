import { readFileSync } from 'node:fs';
import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import type { LegacyCmdbClient } from '../../src/module5/anticorruptionlayer/LegacyCmdbClient.js';
import { LegacyCmdbRecord } from '../../src/module5/anticorruptionlayer/LegacyCmdbRecord.js';
import { DeploymentPlanner } from '../../src/module5/anticorruptionlayer/after/DeploymentPlanner.js';
import { Environment, Tier } from '../../src/module5/anticorruptionlayer/after/Environment.js';
import type { EnvironmentDirectory } from '../../src/module5/anticorruptionlayer/after/EnvironmentDirectory.js';
import { CmdbEnvironmentDirectory } from '../../src/module5/anticorruptionlayer/after/acl/CmdbEnvironmentDirectory.js';
import { LegacyDeploymentPlanner } from '../../src/module5/anticorruptionlayer/before/LegacyDeploymentPlanner.js';
import { LocalDate } from '../../src/module5/support.js';

const TODAY = LocalDate.of(2026, 10, 20);

const CMDB: LegacyCmdbClient = {
  fetchAll: () => [
    new LegacyCmdbRecord('prod-eu', 'P', 1, '20261015'),
    new LegacyCmdbRecord('test-b', 'T', 1, '20260801'),
    new LegacyCmdbRecord('test-a', 'T', 1, '20261019'),
    new LegacyCmdbRecord('dev-1', 'D', 1, '20260901'),
    new LegacyCmdbRecord('dev-old', 'D', 0, '20250101'),
    new LegacyCmdbRecord('prod-us', 'P', 9, '20240101'),
  ],
};

function failureMessage(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalArgumentError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalArgumentError');
}

describe('AntiCorruptionLayerEquivalenceTest', () => {
  it('planningRulesGiveTheSameAnswersThroughTheAcl', () => {
    const before = new LegacyDeploymentPlanner(CMDB);
    const after = new DeploymentPlanner(new CmdbEnvironmentDirectory(CMDB));

    expect(after.canaryTargets()).toEqual(before.canaryTargets());
    expect(after.staleEnvironments(TODAY)).toEqual(before.staleEnvironments(TODAY));
    for (const name of ['prod-eu', 'prod-us', 'test-a', 'unknown']) {
      expect(after.isProduction(name), name).toBe(before.isProduction(name));
    }
    expect(after.canaryTargets()).toEqual(['dev-1', 'test-a', 'test-b']);
    expect(after.staleEnvironments(TODAY)).toEqual(['dev-1', 'test-b']);
  });

  it('translatorMapsLegacyCodesToTheDomainModel', () => {
    const environments = new CmdbEnvironmentDirectory(CMDB).all();

    expect(
      environments[0]!.equals(new Environment('prod-eu', Tier.PRODUCTION, true, LocalDate.of(2026, 10, 15))),
    ).toBe(true);
    expect(
      environments.at(-1)!.equals(new Environment('prod-us', Tier.PRODUCTION, false, LocalDate.of(2024, 1, 1))),
    ).toBe(true);
  });

  it('unknownLegacyDataIsRejectedInsideTheAcl', () => {
    const unknownCode = new CmdbEnvironmentDirectory({
      fetchAll: () => [new LegacyCmdbRecord('x', 'Q', 1, '20260101')],
    });
    const badDate = new CmdbEnvironmentDirectory({
      fetchAll: () => [new LegacyCmdbRecord('x', 'T', 1, '2026-01-01')],
    });

    expect(failureMessage(() => unknownCode.all())).toBe('unknown CMDB environment code: Q');
    expect(failureMessage(() => badDate.all())).toBe('invalid CMDB date: 2026-01-01');
  });

  it('domainCanBeTestedWithoutAnyLegacyType', () => {
    const directory: EnvironmentDirectory = {
      all: () => [new Environment('qa', Tier.TEST, true, TODAY)],
    };

    expect(new DeploymentPlanner(directory).canaryTargets()).toEqual(['qa']);
    // Java sprawdza refleksją typy komponentów rekordu Environment. W TS typy znikają
    // w runtime, więc sprawdzamy źródło: model domenowy nie importuje niczego z modelu CMDB.
    const source = readFileSync(
      new URL('../../src/module5/anticorruptionlayer/after/Environment.ts', import.meta.url),
      'utf8',
    );
    const imports = source.split('\n').filter(line => line.startsWith('import '));
    expect(imports.length).toBeGreaterThan(0);
    for (const line of imports) {
      expect(line, line).not.toMatch(/LegacyCmdb|\/acl\//);
    }
  });
});
