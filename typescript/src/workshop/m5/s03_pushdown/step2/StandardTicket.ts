import { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 2: dopłata VIP liczona w podklasie (override surcharge()) - baza nie czyta już pola. */
export class StandardTicket extends Ticket {
  constructor(basePrice: Money) {
    super(basePrice);
  }

  protected override discountPercent(): number {
    return 0;
  }

  protected override surcharge(): Money {
    return this.isVipUpgraded() ? Money.of('10.00') : Money.ZERO;
  }
}
