import { UnsupportedOperationError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 2: bez zmian. */
export class StudentTicket extends Ticket {
  constructor(basePrice: Money) {
    super(basePrice);
  }

  override upgradeToVip(): void {
    throw new UnsupportedOperationError('bilet ulgowy nie ma dopłaty VIP');
  }

  protected override discountPercent(): number {
    return 25;
  }
}
