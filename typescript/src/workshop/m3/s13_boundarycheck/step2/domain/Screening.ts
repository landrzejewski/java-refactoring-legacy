import type { LocalDateTime } from '../../../../shared/time.js';

/** Pojęcie domenowe: seans. */
export class Screening {
  constructor(readonly title: string, readonly start: LocalDateTime) {}

  equals(other: unknown): boolean {
    return other instanceof Screening && this.title === other.title && this.start.equals(other.start);
  }
}
