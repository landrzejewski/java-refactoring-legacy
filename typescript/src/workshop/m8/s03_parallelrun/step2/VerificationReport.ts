import { assertNever } from '../../../../shared/assertNever.js';
import type { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

export class Agreement {
  readonly kind = 'agreement';

  constructor(readonly query: TicketQuery, readonly price: Money) {}
}

export class Divergence {
  readonly kind = 'divergence';

  constructor(readonly query: TicketQuery, readonly legacy: Money, readonly candidate: Money) {}
}

export class CandidateFailure {
  readonly kind = 'candidateFailure';

  constructor(readonly query: TicketQuery, readonly legacy: Money, readonly error: string) {}
}

export type Verification = Agreement | Divergence | CandidateFailure;

/**
 * Krok 2: raport rozbieżności - zgodność, rozbieżność albo awaria kandydata.
 * Każde porównanie to typowane zdarzenie, a nie wpis w logu do parsowania.
 */
export class VerificationReport {
  private readonly entryList: Verification[] = [];

  record(verification: Verification): void {
    this.entryList.push(verification);
  }

  entries(): readonly Verification[] {
    return Object.freeze([...this.entryList]);
  }

  /** Czytelne wiersze raportu - tylko to, co wymaga uwagi zespołu. */
  problems(): string[] {
    const lines: string[] = [];
    for (const entry of this.entryList) {
      switch (entry.kind) {
        case 'agreement': break;
        case 'divergence':
          lines.push(`ROZBIEZNOSC ${entry.query.toString()}: legacy ${entry.legacy.toString()}, kandydat ${entry.candidate.toString()}`);
          break;
        case 'candidateFailure':
          lines.push(`BLAD KANDYDATA ${entry.query.toString()}: ${entry.error}`);
          break;
        default: assertNever(entry);
      }
    }
    return lines;
  }
}
