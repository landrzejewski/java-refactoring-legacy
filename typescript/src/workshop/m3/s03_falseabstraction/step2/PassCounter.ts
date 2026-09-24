import { Decimal } from 'decimal.js';

/**
 * Krok 2 (rozwiązanie): karnet to osobna wiedza z osobnym właścicielem.
 * Nie wie nic o formatach, porankach ani okularach.
 */
export class PassCounter {
  private static readonly PRICE_PER_ENTRY = new Decimal('20.00');

  pass(entries: number): Decimal {
    return PassCounter.PRICE_PER_ENTRY.times(entries);
  }
}
