import { Quote } from './Quote.js';

/**
 * Krok 1: spełnienie R1 - cennik nie zna powiadomień. Zwraca Quote z flagą rabatu,
 * a decyzję o mailu podejmuje BookingService. R2 (number) nadal naruszona.
 */
export class TicketPricing {
  total(tickets: number, unitPrice: number): Quote {
    let sum = unitPrice * tickets;
    const groupDiscount = tickets >= 10;
    if (groupDiscount) {
      sum = sum - sum * 0.10;
    }
    return new Quote(Math.round(sum * 100) / 100.0, groupDiscount);
  }
}
