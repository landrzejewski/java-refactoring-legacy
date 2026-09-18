import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../src/shared/errors.js';
import { Artifact } from '../../src/module7/returnasap/Artifact.js';
import { ArtifactFinder } from '../../src/module7/returnasap/after/ArtifactFinder.js';
import { LegacyArtifactFinder } from '../../src/module7/returnasap/before/LegacyArtifactFinder.js';

const NULL = null as never;

function messageOf(action: () => unknown): string {
  try {
    action();
  } catch (error) {
    expect(error).toBeInstanceOf(NullPointerError);
    return (error as Error).message;
  }
  throw new Error('Expected action to throw');
}

// Optional.orElseThrow()
function orElseThrow<T>(value: T | undefined): T {
  if (value === undefined) {
    throw new Error('No value present');
  }
  return value;
}

// Odpowiednik AbstractList z nadpisanymi size()/get(): Proxy rejestruje
// odczyty `length` (size) i indeksów (get:i).
function traced(
  artifacts: readonly Artifact[],
  trace: string[],
): readonly Artifact[] {
  return new Proxy(artifacts, {
    get(target, property, receiver) {
      if (property === 'length') {
        trace.push('size');
      } else if (typeof property === 'string' && /^\d+$/.test(property)) {
        trace.push('get:' + property);
      }
      return Reflect.get(target, property, receiver);
    },
  });
}

describe('ReturnAsapEquivalenceTest', () => {
  const before = new LegacyArtifactFinder();
  const after = new ArtifactFinder();

  function assertSameFailure(artifacts: readonly Artifact[], checksum: string): void {
    expect(messageOf(() => after.findByChecksum(artifacts, checksum)))
      .toBe(messageOf(() => before.findByChecksum(artifacts, checksum)));
  }

  it('earlyReturnPreservesEmptyMissingAndPresentResults', () => {
    const first = new Artifact('api.jar', 'sha-1');
    const second = new Artifact('worker.jar', 'sha-2');
    const artifacts = [first, second];

    expect(after.findByChecksum([], 'missing'))
      .toEqual(before.findByChecksum([], 'missing'));
    expect(after.findByChecksum(artifacts, 'missing'))
      .toEqual(before.findByChecksum(artifacts, 'missing'));
    expect(orElseThrow(after.findByChecksum(artifacts, 'sha-1'))).toBe(first);
    expect(orElseThrow(after.findByChecksum(artifacts, 'sha-2'))).toBe(second);
    expect(after.findByChecksum(artifacts, 'sha-2'))
      .toEqual(before.findByChecksum(artifacts, 'sha-2'));
  });

  it('bothVersionsReturnTheFirstMatchingInstance', () => {
    const first = new Artifact('api.jar', 'same');
    const second = new Artifact('worker.jar', 'same');
    const artifacts = [first, second];

    expect(orElseThrow(before.findByChecksum(artifacts, 'same'))).toBe(first);
    expect(orElseThrow(after.findByChecksum(artifacts, 'same'))).toBe(first);
  });

  it('nullElementIsReadBeforeAMatchButNotAfterIt', () => {
    const matching = new Artifact('api.jar', 'sha-1');
    const nullBeforeMatch: Artifact[] = [NULL, matching];
    const nullAfterMatch: Artifact[] = [matching, NULL];

    expect(messageOf(() => after.findByChecksum(nullBeforeMatch, 'sha-1')))
      .toBe(messageOf(() => before.findByChecksum(nullBeforeMatch, 'sha-1')));
    expect(orElseThrow(before.findByChecksum(nullAfterMatch, 'sha-1'))).toBe(matching);
    expect(orElseThrow(after.findByChecksum(nullAfterMatch, 'sha-1'))).toBe(matching);
  });

  it('inputValidationHasTheSameOrderAndMessages', () => {
    assertSameFailure(NULL, 'sha-1');
    assertSameFailure([], NULL);
  });

  it('earlyReturnPreservesIndexedListAccesses', () => {
    const artifacts = [
      new Artifact('api.jar', 'sha-1'),
      new Artifact('worker.jar', 'sha-2')];
    const legacyTrace: string[] = [];
    const refactoredTrace: string[] = [];

    before.findByChecksum(traced(artifacts, legacyTrace), 'sha-1');
    after.findByChecksum(traced(artifacts, refactoredTrace), 'sha-1');

    expect(legacyTrace).toEqual(['size', 'get:0']);
    expect(refactoredTrace).toEqual(legacyTrace);
  });
});
