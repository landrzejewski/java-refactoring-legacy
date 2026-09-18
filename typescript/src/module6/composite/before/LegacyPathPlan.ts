import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { addExact, isBlank } from '../../support.js';

// Odpowiednik rekordu zagnieżdżonego LegacyPathPlan.Entry.
export class Entry {
  readonly path: string;
  readonly minutes: number;

  constructor(path: string, minutes: number) {
    this.path = requirePath(path);
    if (!this.path.includes('/')) {
      throw new IllegalArgumentError('task path must contain a parent');
    }
    if (minutes < 0) {
      throw new IllegalArgumentError('minutes must not be negative');
    }
    this.minutes = minutes;
  }
}

export class LegacyPathPlan {
  private readonly entries: readonly Entry[];

  constructor(entries: readonly Entry[]) {
    this.entries = Object.freeze([...requireNonNull(entries, 'entries')]);
  }

  totalMinutes(): number {
    let total = 0;
    for (const entry of this.entries) {
      total = addExact(total, entry.minutes);
    }
    return total;
  }

  taskNamesBelow(path: string): readonly string[] {
    const prefix = requirePath(path);
    return this.entries
      .filter(entry => entry.path.startsWith(prefix + '/'))
      .map(taskName);
  }
}

function taskName(entry: Entry): string {
  return entry.path.substring(entry.path.lastIndexOf('/') + 1);
}

function requirePath(path: string): string {
  requireNonNull(path, 'path');
  if (isBlank(path) || path.startsWith('/') || path.endsWith('/') || path.includes('//')) {
    throw new IllegalArgumentError('invalid path: ' + path);
  }
  for (const segment of path.split('/')) {
    if (isBlank(segment)) {
      throw new IllegalArgumentError('invalid path: ' + path);
    }
  }
  return path;
}
