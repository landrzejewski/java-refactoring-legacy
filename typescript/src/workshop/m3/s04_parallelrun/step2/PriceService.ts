import type { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';
import { CandidatePriceCalculator } from './CandidatePriceCalculator.js';
import { LegacyPriceCalculator } from './LegacyPriceCalculator.js';
import { Agreement, CandidateFailure, Divergence, type Verification, VerificationReport } from './VerificationReport.js';

/**
 * Krok 2: wynik porównania trafia do VerificationReport (co, dla jakiego wejścia, ile).
 * Klient nadal dostaje wynik legacy - raport jest dowodem do decyzji o przełączeniu.
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
