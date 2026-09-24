import type { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';
import { Format } from './Format.js';

/**
 * Krok 3 (alternatywa dla kroku 2, budowana od kroku 1): Replace Type Code with Class -
 * wiedza o cenie należy do formatu. Reguła dnia zostaje zwykłym switchem, bo jest stabilna.
 */
export class ShowPricing {
  price(day: DayOfWeek, format: string): Money {
    return Format.of(format).priceOn(day);
  }
}
