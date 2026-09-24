import type { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';
import { CandidatePriceCalculator } from './CandidatePriceCalculator.js';
import { LegacyPriceCalculator } from './LegacyPriceCalculator.js';

/**
 * Krok 1: dodanie cienia. Kandydat liczy na tym samym wejściu, ale klient zawsze dostaje
 * wynik legacy. Awaria kandydata jest izolowana (try/catch), a nie przerywa sprzedaży.
 */
export class PriceService {
  private readonly legacy = new LegacyPriceCalculator();
  private readonly candidate = new CandidatePriceCalculator();
  private mismatchCount = 0;

  price(query: TicketQuery): Money {
    const result = this.legacy.price(query);
    try {
      if (!this.candidate.price(query).equals(result)) {
        this.mismatchCount++;
      }
    } catch {
      this.mismatchCount++;
    }
    return result;
  }

  /** Wiemy, ŻE coś się różni, ale nie wiemy CO - licznik to za mało do decyzji. */
  mismatches(): number {
    return this.mismatchCount;
  }
}
