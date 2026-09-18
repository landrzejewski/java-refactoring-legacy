import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../src/shared/errors.js';
import { KeyValueReleaseImporter } from '../../src/module6/templatemethod/after/KeyValueReleaseImporter.js';
import { PipeReleaseImporter } from '../../src/module6/templatemethod/after/PipeReleaseImporter.js';
import type { ReleaseDraft } from '../../src/module6/templatemethod/after/ReleaseDraft.js';
import { type Fields, ReleaseImporter } from '../../src/module6/templatemethod/after/ReleaseImporter.js';
import { LegacyKeyValueReleaseImporter } from '../../src/module6/templatemethod/before/LegacyKeyValueReleaseImporter.js';
import { LegacyPipeReleaseImporter } from '../../src/module6/templatemethod/before/LegacyPipeReleaseImporter.js';
import type { ReleaseDraft as LegacyReleaseDraft } from '../../src/module6/templatemethod/before/ReleaseDraft.js';

class CommaReleaseImporter extends ReleaseImporter {
  protected override parse(raw: string): Fields {
    const parts = raw.split(',');
    if (parts.length !== 2) {
      throw new IllegalArgumentError('expected releaseId and service');
    }
    return this.fields(parts[0]!.trim(), parts[1]!.trim());
  }
}

function assertEquivalent(before: LegacyReleaseDraft, after: ReleaseDraft): void {
  expect(after.releaseId).toBe(before.releaseId);
  expect(after.service).toBe(before.service);
}

function captureFailure(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('expected failure');
}

describe('TemplateMethodEquivalenceTest', () => {
  it('commonSkeletonPreservesBothImportFormats', () => {
    const legacyPipe = new LegacyPipeReleaseImporter().importRelease(' rel-42 | payments ');
    const pipe = new PipeReleaseImporter().importRelease(' rel-42 | payments ');
    const legacyKeyValue = new LegacyKeyValueReleaseImporter().importRelease('id=rel-42;service=payments');
    const keyValue = new KeyValueReleaseImporter().importRelease('id=rel-42;service=payments');

    assertEquivalent(legacyPipe, pipe);
    assertEquivalent(legacyKeyValue, keyValue);
  });

  it('templateMethodProtectsTheRequiredOrder', () => {
    // Java sprawdza Modifier.isFinal(importRelease). TS nie ma metod `final`,
    // więc sprawdzamy sens: metoda szablonowa jest zdefiniowana w klasie bazowej
    // i żadna podklasa jej nie nadpisuje (nie ma jej na własnym prototypie).
    expect(Object.getOwnPropertyNames(ReleaseImporter.prototype)).toContain('importRelease');
    for (const subclass of [PipeReleaseImporter, KeyValueReleaseImporter, CommaReleaseImporter]) {
      expect(Object.getOwnPropertyNames(subclass.prototype)).not.toContain('importRelease');
      expect(subclass.prototype.importRelease).toBe(ReleaseImporter.prototype.importRelease);
    }
  });

  it('commonValidationPreservesTheLegacyFailure', () => {
    const before = captureFailure(() => new LegacyPipeReleaseImporter().importRelease(' |payments'));
    const after = captureFailure(() => new PipeReleaseImporter().importRelease(' |payments'));

    expect(after.constructor).toBe(before.constructor);
    expect(after.message).toBe(before.message);
  });

  it('supportsSubclassOutsideTheImplementationPackage', () => {
    const imported = new CommaReleaseImporter().importRelease('rel-42,payments');

    expect(imported.releaseId).toBe('rel-42');
    expect(imported.service).toBe('payments');
  });
});
