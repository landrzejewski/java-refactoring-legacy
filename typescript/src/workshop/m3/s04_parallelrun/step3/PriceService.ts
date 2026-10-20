import type { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';
import { CandidatePriceCalculator } from './CandidatePriceCalculator.js';
import { LegacyPriceCalculator } from './LegacyPriceCalculator.js';
import { Agreement, CandidateFailure, Divergence, type Verification, VerificationReport } from './VerificationReport.js';

/**
 * Krok 3 (bez zmian): tryb shadow z raportem. Po poprawce kandydata raport nie ma rozbieżności,
 * zostaje tylko świadomie zaakceptowana różnica dla nieznanego formatu.
 */
export class PriceService {
  private readonly legacy = new LegacyPriceCalculator();
  private readonly candidate = new CandidatePriceCalculator();

  constructor(private readonly report: VerificationReport = new VerificationReport()) {}

  price(query: TicketQuery): Money {
    const result = this.legacy.price(query);
    this.report.record(this.verify(query, result));
    return result;
  }

  private verify(query: TicketQuery, legacyPrice: Money): Verification {
    try {
      const candidatePrice = this.candidate.price(query);
      return legacyPrice.equals(candidatePrice)
        ? new Agreement(query, legacyPrice)
        : new Divergence(query, legacyPrice, candidatePrice);
    } catch (failure) {
      const error = failure as Error;
      return new CandidateFailure(query, legacyPrice, error.name + ': ' + error.message);
    }
  }
}
