import { Money } from '../../../shared/Money.js';
import { LocalTime } from '../../../shared/time.js';
import type { GroupOrder } from '../GroupOrder.js';
import { Quote } from '../Quote.js';

/**
 * Krok 3 (rozwiązanie): Extract Method wewnątrz obiektu metody. Pola niosą stan
 * między krokami, więc każdy blok stał się metodą bez parametrów, a calculate()
 * czyta się jak spis treści. Kolejność obliczeń bez zmian.
 */
export class GroupQuoteCalculation {
  private base!: Money;
  private morning = false;
  private glasses = false;
  private tickets = Money.ZERO;
  private count = 0;

  constructor(private readonly order: GroupOrder) {}

  calculate(): Quote {
    this.readConditions();
    this.addTickets();
    this.addVipSeats();
    this.applyGroupDiscount();
    const fees = this.bookingFees();
    return new Quote(this.tickets, fees, this.tickets.plus(fees), this.loyaltyPoints());
  }

  private readConditions(): void {
    switch (this.order.format) {
      case 'IMAX': this.base = Money.of('40.00'); break;
      case '3D': this.base = Money.of('32.00'); break;
      default: this.base = Money.of('25.00');
    }
    this.morning = this.order.start.isBefore(LocalTime.NOON);
    this.glasses = this.order.format === '3D' && !this.order.ownGlasses;
  }

  private addTickets(): void {
    for (const type of this.order.ticketTypes) {
      this.tickets = this.tickets.plus(this.ticketPrice(type));
      this.count++;
    }
  }

  private ticketPrice(type: string): Money {
    let price = this.base.minus(this.base.percent(GroupQuoteCalculation.discountPercent(type)));
    if (this.morning) {
      price = price.minus(Money.of('5.00'));
    }
    if (this.glasses) {
      price = price.plus(Money.of('3.00'));
    }
    return price;
  }

  private static discountPercent(type: string): number {
    switch (type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return 0;
    }
  }

  private addVipSeats(): void {
    this.tickets = this.tickets.plus(Money.of('10.00').times(this.order.vipSeats));
  }

  private applyGroupDiscount(): void {
    if (this.count >= 10) {
      this.tickets = this.tickets.minus(this.tickets.percent(10));
    }
  }

  private bookingFees(): Money {
    return this.order.online ? Money.of('2.00').times(this.count) : Money.ZERO;
  }

  private loyaltyPoints(): number {
    return Math.trunc(this.tickets.amount.trunc().toNumber() / 10);
  }
}
