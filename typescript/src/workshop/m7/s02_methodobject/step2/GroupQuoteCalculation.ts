import { Money } from '../../../shared/Money.js';
import { LocalTime } from '../../../shared/time.js';
import type { GroupOrder } from '../GroupOrder.js';
import { Quote } from '../Quote.js';

/**
 * Krok 2: wartości robocze (base, morning, glasses, tickets, count) stają się polami.
 * Kod metody się nie zmienia poza deklaracjami - teraz każdy blok da się wydzielić
 * bez przekazywania parametrów i bez wielu wyjść.
 */
export class GroupQuoteCalculation {
  private base!: Money;
  private morning = false;
  private glasses = false;
  private tickets = Money.ZERO;
  private count = 0;

  constructor(private readonly order: GroupOrder) {}

  calculate(): Quote {
    switch (this.order.format) {
      case 'IMAX': this.base = Money.of('40.00'); break;
      case '3D': this.base = Money.of('32.00'); break;
      default: this.base = Money.of('25.00');
    }
    this.morning = this.order.start.isBefore(LocalTime.NOON);
    this.glasses = this.order.format === '3D' && !this.order.ownGlasses;
    for (const type of this.order.ticketTypes) {
      let discount: number;
      switch (type) {
        case 'STUDENT': discount = 25; break;
        case 'SENIOR': discount = 30; break;
        case 'CHILD': discount = 40; break;
        default: discount = 0;
      }
      let price = this.base.minus(this.base.percent(discount));
      if (this.morning) {
        price = price.minus(Money.of('5.00'));
      }
      if (this.glasses) {
        price = price.plus(Money.of('3.00'));
      }
      this.tickets = this.tickets.plus(price);
      this.count++;
    }
    this.tickets = this.tickets.plus(Money.of('10.00').times(this.order.vipSeats));
    if (this.count >= 10) {
      this.tickets = this.tickets.minus(this.tickets.percent(10));
    }
    const fees = this.order.online ? Money.of('2.00').times(this.count) : Money.ZERO;
    const total = this.tickets.plus(fees);
    const points = Math.trunc(this.tickets.amount.trunc().toNumber() / 10);
    return new Quote(this.tickets, fees, total, points);
  }
}
