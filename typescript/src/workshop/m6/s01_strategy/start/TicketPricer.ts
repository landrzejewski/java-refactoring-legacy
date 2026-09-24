import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';

/**
 * Start: algorytm zniżki wybierany łańcuchem if po nazwie programu. Każdy nowy program
 * (np. "tydzień studenta") dopisuje gałąź w środku metody, która zawiera też walidację.
 */
export class TicketPricer {
  price(base: Money, ticketType: string, program: string | null): Money {
    if (base.compareTo(Money.ZERO) < 0) {
      throw new IllegalArgumentError('base price must not be negative');
    }
    if (program === null) {
      throw new IllegalArgumentError('program must not be null');
    }
    let discount: Money;
    if (program === 'PREMIERE') {
      discount = Money.ZERO;
    } else if (program === 'STUDENT_WEEK' && ticketType === 'S') {
      discount = base.percent(50);
    } else if (program === 'STANDARD' || program === 'STUDENT_WEEK') {
      let percent: number;
      switch (ticketType) {
        case 'N': percent = 0; break;
        case 'S': percent = 25; break;
        case 'E': percent = 30; break;
        case 'C': percent = 40; break;
        default: throw new IllegalArgumentError(`unknown ticket type: ${ticketType}`);
      }
      discount = base.percent(percent);
    } else {
      throw new IllegalArgumentError(`unknown program: ${program}`);
    }
    return base.minus(discount);
  }
}
