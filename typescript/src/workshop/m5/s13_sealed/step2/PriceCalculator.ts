import { assertNever } from '../../../../shared/assertNever.js';
import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/**
 * Krok 2: Replace Conditional with switch po `kind` - wyczerpujący, bez gałęzi z wynikiem domyślnym.
 * `default` przekazuje resztę do assertNever(ticket: never): jeśli jakiś wariant nie ma swojego `case`,
 * reszta nie jest `never` i kompilacja się nie uda. Każdy wariant jest wymieniony jawnie, także STANDARD (0%).
 */
export class PriceCalculator {
  discountPercent(ticket: Ticket): number {
    switch (ticket.kind) {
      case 'STANDARD': return 0;
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      default: return assertNever(ticket);
    }
  }

  price(ticket: Ticket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(this.discountPercent(ticket)));
  }
}
