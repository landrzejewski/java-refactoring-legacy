import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import { type ChangeEntry, Kind } from '../ChangeEntry.js';

/**
 * Strategia zakresu treści - dawny krok include szablonu razem z tytułem.
 */
export class ContentSelection {
  static readonly CHANGELOG = new ContentSelection('Changelog', () => true);
  static readonly HIGHLIGHTS = new ContentSelection('Highlights', entry => entry.kind !== Kind.FIX);

  constructor(
    readonly title: string,
    readonly filter: (entry: ChangeEntry) => boolean,
  ) {
    if (isBlank(title)) {
      throw new IllegalArgumentError('title must not be blank');
    }
    requireNonNull(filter, 'filter');
  }

  includes(entry: ChangeEntry): boolean {
    return this.filter(entry);
  }
}
