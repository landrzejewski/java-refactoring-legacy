import type { LocalDateTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny - seans. */
export class Screening {
  constructor(readonly title: string, readonly format: string, readonly start: LocalDateTime) {}
}
