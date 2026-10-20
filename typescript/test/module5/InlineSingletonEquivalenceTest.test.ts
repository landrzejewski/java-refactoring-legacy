import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, UnsupportedOperationError } from '../../src/shared/errors.js';
import { FeatureFlags } from '../../src/module5/inlinesingleton/after/FeatureFlags.js';
import { RolloutCompositionRoot } from '../../src/module5/inlinesingleton/after/RolloutCompositionRoot.js';
import { RolloutService } from '../../src/module5/inlinesingleton/after/RolloutService.js';
import { LegacyFeatureFlags } from '../../src/module5/inlinesingleton/before/LegacyFeatureFlags.js';
import { LegacyRolloutService } from '../../src/module5/inlinesingleton/before/LegacyRolloutService.js';

const COMBINATIONS: readonly (readonly string[])[] = [
  [],
  ['canary'],
  ['dark-launch'],
  ['canary', 'dark-launch'],
  ['unrelated'],
];

function failureMessage(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalArgumentError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalArgumentError');
}

// @ResourceLock z Javy niepotrzebny: Vitest uruchamia testy jednego pliku po kolei,
// a każdy plik ma własny graf modułów (własną instancję LegacyFeatureFlags).
describe('InlineSingletonEquivalenceTest', () => {
  it('planIsTheSameForEveryFlagCombination', () => {
    const legacy = new LegacyRolloutService();
    try {
      for (const combination of COMBINATIONS) {
        // stary kod: trzeba zmienić globalny stan i pamiętać o sprzątaniu
        LegacyFeatureFlags.getInstance().reset();
        combination.forEach(flag => LegacyFeatureFlags.getInstance().enable(flag));

        expect(new RolloutService(new FeatureFlags(combination)).plan('rel-42'), String(combination)).toBe(
          legacy.plan('rel-42'),
        );
      }
    } finally {
      LegacyFeatureFlags.getInstance().reset();
    }
  });

  it('servicesWithDifferentFlagsCanCoexistWithoutSharedState', () => {
    const canary = new RolloutService(FeatureFlags.of('canary'));
    const plain = new RolloutService(FeatureFlags.of());

    expect(canary.plan('rel-1')).toBe('rel-1: 10% -> 50% -> 100%');
    expect(plain.plan('rel-1')).toBe('rel-1: 100%');
    expect(canary.plan('rel-1')).toBe('rel-1: 10% -> 50% -> 100%');
  });

  it('legacySingletonLeaksStateBetweenUsers', () => {
    const first = new LegacyRolloutService();
    const second = new LegacyRolloutService();
    try {
      LegacyFeatureFlags.getInstance().enable('canary');

      expect(second.plan('rel-1')).toBe('rel-1: 10% -> 50% -> 100%');
      expect(second.plan('rel-1')).toBe(first.plan('rel-1'));
    } finally {
      LegacyFeatureFlags.getInstance().reset();
    }
  });

  it('compositionRootBuildsTheServiceFromConfiguration', () => {
    expect(RolloutCompositionRoot.create(' canary , dark-launch ').plan('rel-7')).toBe(
      'rel-7: 10% -> 50% -> 100% (dark launch)',
    );
    expect(RolloutCompositionRoot.create('').plan('rel-7')).toBe('rel-7: 100%');
  });

  it('flagsAreImmutableValues', () => {
    const source = new Set(['canary']);
    const flags = new FeatureFlags(source);
    source.clear();

    expect(flags.equals(FeatureFlags.of('canary'))).toBe(true);
    // ReadonlySet nie ma add w typie; rzutowanie odpowiada wywołaniu add na Set.copyOf w Javie.
    expect(() => (flags.enabled as Set<string>).add('x')).toThrow(UnsupportedOperationError);
    expect(failureMessage(() => new RolloutService(FeatureFlags.of()).plan(' '))).toBe(
      failureMessage(() => new LegacyRolloutService().plan(' ')),
    );
  });
});
