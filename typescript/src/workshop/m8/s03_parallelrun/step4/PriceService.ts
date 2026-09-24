import { assertNever } from '../../../../shared/assertNever.js';
import type { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';
import { CandidatePriceCalculator } from './CandidatePriceCalculator.js';
import { LegacyPriceCalculator } from './LegacyPriceCalculator.js';
import { MigrationMode } from './MigrationMode.js';
import { Agreement, CandidateFailure, Divergence, VerificationReport } from './VerificationReport.js';

/**
 * Krok 4: przełączenie. Wybór ścieżki w jednym miejscu; CANDIDATE czyni nowy kalkulator
 * autorytatywnym, a LEGACY pozostaje natychmiastowym wycofaniem aż do usunięcia starego kodu.
 */
export class PriceService {
  private readonly legacy = new LegacyPriceCalculator();
  private readonly candidate = new CandidatePriceCalculator();

  constructor(
    private readonly mode: MigrationMode = MigrationMode.SHADOW,
    private readonly report: VerificationReport = new VerificationReport(),
  ) {}

  price(query: TicketQuery): Money {
    switch (this.mode) {
      case MigrationMode.LEGACY: return this.legacy.price(query);
      case MigrationMode.SHADOW: return this.shadow(query);
      case MigrationMode.CANDIDATE: return this.candidate.price(query);
      default: return assertNever(this.mode);
    }
  }

  private shadow(query: TicketQuery): Money {
    const result = this.legacy.price(query);
    try {
      const candidatePrice = this.candidate.price(query);
      this.report.record(result.equals(candidatePrice)
        ? new Agreement(query, result)
        : new Divergence(query, result, candidatePrice));
    } catch (failure) {
      const error = failure as Error;
      this.report.record(new CandidateFailure(query, result, error.name + ': ' + error.message));
    }
    return result;
  }
}
