import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { CustomerTier } from '../../src/module5/variability/CustomerTier.js';
import { ReleaseConsole } from '../../src/module5/variability/after/ReleaseConsole.js';
import { SupportCostPolicy } from '../../src/module5/variability/after/SupportCostPolicy.js';
import { LegacyReleaseConsole } from '../../src/module5/variability/before/LegacyReleaseConsole.js';

type Consumer<T> = (value: T) => void;

function captureFailure(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('expected failure');
}

function assertSameLifecycle(before: LegacyReleaseConsole, after: ReleaseConsole): void {
  expect(after.status()).toBe(before.status());
  expect(after.banner()).toBe(before.banner());
}

function assertSameFailureOn(
  before: LegacyReleaseConsole,
  after: ReleaseConsole,
  beforeAction: Consumer<LegacyReleaseConsole>,
  afterAction: Consumer<ReleaseConsole>,
): void {
  const beforeFailure = captureFailure(() => beforeAction(before));
  const afterFailure = captureFailure(() => afterAction(after));

  expect(afterFailure.constructor).toBe(beforeFailure.constructor);
  expect(afterFailure.message).toBe(beforeFailure.message);
  expect(after.status()).toBe(before.status());
  expect(after.log()).toEqual(before.log());
}

function assertSameFailure(
  beforeAction: Consumer<LegacyReleaseConsole>,
  afterAction: Consumer<ReleaseConsole>,
): void {
  assertSameFailureOn(
    new LegacyReleaseConsole(CustomerTier.STANDARD),
    ReleaseConsole.forTier(CustomerTier.STANDARD),
    beforeAction,
    afterAction,
  );
}

describe('VariabilityEquivalenceTest', () => {
  // Odpowiednik @ParameterizedTest + @EnumSource(CustomerTier.class).
  it.each(Object.values(CustomerTier))('strategyAxisPreservesEveryTierCost(%s)', tier => {
    const before = new LegacyReleaseConsole(tier);
    const after = ReleaseConsole.forTier(tier);

    for (const hours of [0, 1, 10, 33, 100]) {
      expect(after.supportCost(hours)).toBe(before.supportCost(hours));
    }
    const beforeFailure = captureFailure(() => before.supportCost(-1));
    const afterFailure = captureFailure(() => after.supportCost(-1));
    expect(beforeFailure).toBeInstanceOf(IllegalArgumentError);
    expect(afterFailure).toBeInstanceOf(IllegalArgumentError);
    expect(afterFailure.message).toBe(beforeFailure.message);
  });

  it('strategyCanBeSuppliedByTheClientWithoutTouchingTheConsole', () => {
    const flatRate = new ReleaseConsole({ cost: () => 1_000 });

    expect(flatRate.supportCost(7)).toBe(1_000);
    expect(new ReleaseConsole(SupportCostPolicy.PREMIUM).supportCost(10)).toBe(1_300);
  });

  it('commandAxisPreservesQueueLogAndUndo', () => {
    const before = new LegacyReleaseConsole(CustomerTier.STANDARD);
    const after = ReleaseConsole.forTier(CustomerTier.STANDARD);

    for (const action of ['scale-up', 'maintenance', 'scale-down', 'scale-down']) {
      before.submit(action);
      after.submit(action);
    }
    expect(after.log()).toEqual(before.log());
    before.runQueued();
    after.runQueued();
    before.undoLast();
    after.undoLast();
    before.undoLast();
    after.undoLast();

    expect(after.log()).toEqual([
      'scale-up -> instances=3, maintenance=off',
      'maintenance -> instances=3, maintenance=on',
      'scale-down -> instances=2, maintenance=on',
      'scale-down -> instances=1, maintenance=on',
      'undo scale-down -> instances=2, maintenance=on',
      'undo scale-down -> instances=3, maintenance=on',
    ]);
    expect(after.log()).toEqual(before.log());
  });

  it('commandAxisPreservesFailures', () => {
    assertSameFailure(console => console.submit('reboot'), console => console.submit('reboot'));
    assertSameFailure(console => console.undoLast(), console => console.undoLast());
    assertSameFailure(
      console => {
        console.submit('scale-down');
        console.submit('scale-down');
        console.runQueued();
      },
      console => {
        console.submit('scale-down');
        console.submit('scale-down');
        console.runQueued();
      },
    );
  });

  it('stateAxisPreservesLifecycleAndBanners', () => {
    const before = new LegacyReleaseConsole(CustomerTier.STANDARD);
    const after = ReleaseConsole.forTier(CustomerTier.STANDARD);
    assertSameLifecycle(before, after);

    before.advance();
    after.advance();
    assertSameLifecycle(before, after);
    before.advance();
    after.advance();
    assertSameLifecycle(before, after);
    assertSameFailureOn(before, after, console => console.advance(), console => console.advance());
    before.rollback();
    after.rollback();
    assertSameLifecycle(before, after);
    assertSameFailureOn(before, after, console => console.rollback(), console => console.rollback());
    assertSameFailureOn(before, after, console => console.advance(), console => console.advance());
  });

  it('plannedReleaseCannotBeRolledBackInBothVersions', () => {
    assertSameFailure(console => console.rollback(), console => console.rollback());
  });
});
