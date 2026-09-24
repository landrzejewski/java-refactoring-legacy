import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';

/**
 * Start: rdzeń biletu obrośnięty flagami dodatków. Każdy nowy dodatek to kolejne pole,
 * kolejny if w price() i description() - a większość biletów nie ma żadnego dodatku.
 */
export class Ticket {
  private readonly title: string;
  private readonly format: string;
  private readonly base: Money;
  private readonly vip: boolean;
  private readonly glasses: boolean;
  private readonly insurance: boolean;

  constructor(order: TicketOrder) {
    this.title = order.title;
    this.format = order.format;
    this.base = order.base;
    this.vip = order.vip;
    this.glasses = order.format === '3D' && !order.ownGlasses;
    this.insurance = order.insurance;
  }

  price(): Money {
    let price = this.base;
    if (this.vip) {
      price = price.plus(Money.of('10.00'));
    }
    if (this.glasses) {
      price = price.plus(Money.of('3.00'));
    }
    if (this.insurance) {
      price = price.plus(Money.of('4.00'));
    }
    return price;
  }

  description(): string {
    return `${this.title} ${this.format}`
      + (this.vip ? ' +VIP' : '')
      + (this.glasses ? ' +okulary 3D' : '')
      + (this.insurance ? ' +ubezpieczenie' : '');
  }
}
