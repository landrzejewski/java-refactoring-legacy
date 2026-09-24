import { Money } from '../../../shared/Money.js';
import { Ticket } from './Ticket.js';

/** Krok 3: stan i operacja VIP żyją tam, gdzie mają sens. */
export class StandardTicket extends Ticket {
  #vipUpgraded = false;

  constructor(basePrice: Money) {
    super(basePrice);
  }

  upgradeToVip(): void {
    this.#vipUpgraded = true;
  }

  isVipUpgraded(): boolean {
    return this.#vipUpgraded;
  }

  protected override discountPercent(): number {
    return 0;
  }

  protected override surcharge(): Money {
    return this.isVipUpgraded() ? Money.of('10.00') : Money.ZERO;
  }
}
