import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { PricedTicket } from './PricedTicket.js';

/** Krok 2: flaga insurance usunięta z rdzenia - zajmuje się nią dekorator Insurance. */
export class Ticket implements PricedTicket {
  private readonly title: string;
  private readonly format: string;
  private readonly base: Money;
  private readonly vip: boolean;
  private readonly glasses: boolean;

  constructor(order: TicketOrder) {
    this.title = order.title;
    this.format = order.format;
    this.base = order.base;
    this.vip = order.vip;
    this.glasses = order.format === '3D' && !order.ownGlasses;
  }

  price(): Money {
    let price = this.base;
    if (this.vip) {
      price = price.plus(Money.of('10.00'));
    }
    if (this.glasses) {
      price = price.plus(Money.of('3.00'));
    }
    return price;
  }

  description(): string {
    return `${this.title} ${this.format}`
      + (this.vip ? ' +VIP' : '')
      + (this.glasses ? ' +okulary 3D' : '');
  }
}
