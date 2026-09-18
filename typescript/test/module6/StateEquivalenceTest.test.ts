import { describe, expect, it } from 'vitest';
import { IllegalStateError } from '../../src/shared/errors.js';
import { Release } from '../../src/module6/state/after/Release.js';
import { LegacyRelease } from '../../src/module6/state/before/LegacyRelease.js';

type Action<T> = (release: T) => void;

function deploy(release: LegacyRelease | Release): void {
  release.approve();
  release.deploy();
}

const noop = (): void => {};
const approve = (release: LegacyRelease | Release): void => release.approve();
const deployAction = (release: LegacyRelease | Release): void => release.deploy();
const cancel = (release: LegacyRelease | Release): void => release.cancel();

function failureMessage(action: () => void): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(IllegalStateError);
    return (error as Error).message;
  }
  throw new Error('expected IllegalStateError');
}

function assertInvalid(
  prepareBefore: Action<LegacyRelease>,
  prepareAfter: Action<Release>,
  actionBefore: Action<LegacyRelease>,
  actionAfter: Action<Release>,
): void {
  const before = new LegacyRelease();
  const after = new Release();
  prepareBefore(before);
  prepareAfter(after);
  const status: string = before.status();
  expect(after.status()).toBe(status);

  const beforeFailure = failureMessage(() => actionBefore(before));
  const afterFailure = failureMessage(() => actionAfter(after));

  expect(afterFailure).toBe(beforeFailure);
  expect(before.status()).toBe(status);
  expect(after.status()).toBe(status);
}

describe('StateEquivalenceTest', () => {
  it('preservesApprovalAndDeploymentTransitions', () => {
    const before = new LegacyRelease();
    const after = new Release();

    expect(after.status()).toBe(before.status());
    before.approve();
    after.approve();
    expect(after.status()).toBe(before.status());
    before.deploy();
    after.deploy();
    expect(after.status()).toBe(before.status());
  });

  it('preservesBothAllowedCancellationPaths', () => {
    const draftBefore = new LegacyRelease();
    const draftAfter = new Release();
    draftBefore.cancel();
    draftAfter.cancel();
    expect(draftAfter.status()).toBe(draftBefore.status());

    const approvedBefore = new LegacyRelease();
    const approvedAfter = new Release();
    approvedBefore.approve();
    approvedAfter.approve();
    approvedBefore.cancel();
    approvedAfter.cancel();
    expect(approvedAfter.status()).toBe(approvedBefore.status());
  });

  it('everyInvalidTransitionKeepsStateAndExceptionMessage', () => {
    assertInvalid(noop, noop, deployAction, deployAction);
    assertInvalid(approve, approve, approve, approve);

    assertInvalid(deploy, deploy, approve, approve);
    assertInvalid(deploy, deploy, deployAction, deployAction);
    assertInvalid(deploy, deploy, cancel, cancel);

    assertInvalid(cancel, cancel, approve, approve);
    assertInvalid(cancel, cancel, deployAction, deployAction);
    assertInvalid(cancel, cancel, cancel, cancel);
  });
});
