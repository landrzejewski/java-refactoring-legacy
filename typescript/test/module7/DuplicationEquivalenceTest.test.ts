import { describe, expect, it } from 'vitest';
import { ArtifactPublisher } from '../../src/module7/duplication/after/ArtifactPublisher.js';
import { LegacyArtifactPublisher } from '../../src/module7/duplication/before/LegacyArtifactPublisher.js';

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

describe('DuplicationEquivalenceTest', () => {
  it('preservesBothPublicationVariantsIndependentlyOfDefaultLocale', () => {
    // JS nie ma globalnego Locale.setDefault. Zamiast tego pokazujemy, że
    // wynik różniłby się, gdyby kod używał operacji zależnych od locale:
    // w tr-TR wielkie „I” zamienia się na „ı” (bez kropki).
    expect(' IMAGE '.trim().toLocaleLowerCase('tr-TR')).toBe('ımage');
    expect(' IMAGE '.trim().toLowerCase()).toBe('image');

    const legacy = new LegacyArtifactPublisher();
    const refactored = new ArtifactPublisher();

    expect(legacy.publishSnapshot(' IMAGE ', 42)).toBe('image:42-SNAPSHOT');
    expect(refactored.publishSnapshot(' IMAGE ', 42))
      .toBe(legacy.publishSnapshot(' IMAGE ', 42));
    expect(legacy.publishRelease(' IMAGE ', 42)).toBe('image:42');
    expect(refactored.publishRelease(' IMAGE ', 42))
      .toBe(legacy.publishRelease(' IMAGE ', 42));
    expect(refactored.publishRelease(' IMAGE ', 42))
      .not.toBe(' IMAGE '.trim().toLocaleLowerCase('tr-TR') + ':42');
  });

  it('preservesValidationTypeMessageAndOrderForBothVariants', () => {
    const legacy = new LegacyArtifactPublisher();
    const refactored = new ArtifactPublisher();

    assertSameFailure(
      () => legacy.publishSnapshot(NULL, 0),
      () => refactored.publishSnapshot(NULL, 0));
    assertSameFailure(
      () => legacy.publishSnapshot(' \t', 0),
      () => refactored.publishSnapshot(' \t', 0));
    assertSameFailure(
      () => legacy.publishSnapshot('api', 0),
      () => refactored.publishSnapshot('api', 0));

    assertSameFailure(
      () => legacy.publishRelease(NULL, 0),
      () => refactored.publishRelease(NULL, 0));
    assertSameFailure(
      () => legacy.publishRelease(' \t', 0),
      () => refactored.publishRelease(' \t', 0));
    assertSameFailure(
      () => legacy.publishRelease('api', 0),
      () => refactored.publishRelease('api', 0));
  });
});
