import { describe, expect, it } from 'vitest';
import { ManifestEntry } from '../../src/module7/breakmethod/ManifestEntry.js';
import { ReleaseManifestBuilder } from '../../src/module7/breakmethod/after/ReleaseManifestBuilder.js';
import { LegacyReleaseManifestBuilder } from '../../src/module7/breakmethod/before/LegacyReleaseManifestBuilder.js';

const NULL = null as never;

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function assertSameFailure(
  legacyCall: () => string,
  refactoredCall: () => string,
): void {
  const legacyFailure = failureOf(legacyCall);
  const refactoredFailure = failureOf(refactoredCall);

  expect(refactoredFailure.constructor).toBe(legacyFailure.constructor);
  expect(refactoredFailure.message).toBe(legacyFailure.message);
}

function listContainingNull(): ManifestEntry[] {
  return [NULL];
}

describe('BreakMethodEquivalenceTest', () => {
  it('preservesOrderingRenderingAndTheCallersCollection', () => {
    const entries = [
      new ManifestEntry('web', 'sha-web', 20),
      new ManifestEntry('worker', 'sha-worker', 10),
      new ManifestEntry('api', 'sha-api', 10)];
    const originalOrder = [...entries];

    const legacy = new LegacyReleaseManifestBuilder();
    const refactored = new ReleaseManifestBuilder();

    const expected = '10|api|sha-api\n'
      + '10|worker|sha-worker\n'
      + '20|web|sha-web';

    expect(legacy.build(entries)).toBe(expected);
    expect(refactored.build(entries)).toBe(expected);
    expect(entries).toEqual(originalOrder);
    expect(legacy.build([])).toBe('');
    expect(refactored.build([])).toBe('');
  });

  it('preservesValidationTypeMessageAndEncounterOrder', () => {
    const legacy = new LegacyReleaseManifestBuilder();
    const refactored = new ReleaseManifestBuilder();

    assertSameFailure(
      () => legacy.build(NULL),
      () => refactored.build(NULL));
    assertSameFailure(
      () => legacy.build(listContainingNull()),
      () => refactored.build(listContainingNull()));
    assertSameFailure(
      () => legacy.build([new ManifestEntry(NULL, NULL, -1)]),
      () => refactored.build([new ManifestEntry(NULL, NULL, -1)]));
    assertSameFailure(
      () => legacy.build([new ManifestEntry(' ', NULL, -1)]),
      () => refactored.build([new ManifestEntry(' ', NULL, -1)]));
    assertSameFailure(
      () => legacy.build([new ManifestEntry('api', NULL, -1)]),
      () => refactored.build([new ManifestEntry('api', NULL, -1)]));
    assertSameFailure(
      () => legacy.build([new ManifestEntry('api', ' ', -1)]),
      () => refactored.build([new ManifestEntry('api', ' ', -1)]));
    assertSameFailure(
      () => legacy.build([new ManifestEntry('api', 'sha', -1)]),
      () => refactored.build([new ManifestEntry('api', 'sha', -1)]));
  });

  it('preservesInputOrderWhenAllSortKeysAreEqual', () => {
    const entries = [
      new ManifestEntry('api', 'sha-first', 10),
      new ManifestEntry('api', 'sha-second', 10)];
    const expected = '10|api|sha-first\n10|api|sha-second';

    expect(new LegacyReleaseManifestBuilder().build(entries)).toBe(expected);
    expect(new ReleaseManifestBuilder().build(entries)).toBe(expected);
  });
});
