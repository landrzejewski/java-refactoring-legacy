import type { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';
import { LegacyPriceCalculator } from './LegacyPriceCalculator.js';

/**
 * Start: usługa korzysta tylko ze starego kalkulatora. Kandydat leży obok nieużywany -
 * jedyne opcje to "włączyć i zobaczyć" albo wieczne testy ręczne.
 */
export class PriceService {
  private readonly legacy = new LegacyPriceCalculator();

  price(query: TicketQuery): Money {
    return this.legacy.price(query);
  }
}
