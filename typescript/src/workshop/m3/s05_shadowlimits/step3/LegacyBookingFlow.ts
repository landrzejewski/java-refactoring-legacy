import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Infrastructure } from '../Infrastructure.js';

/** Krok 3 (bez zmian): stara ścieżka rezerwacji - autorytatywna, wykonuje prawdziwe efekty. */
export class LegacyBookingFlow {
  constructor(private readonly infra: Infrastructure) {}

  book(r: BookingRequest): string {
    const total = 25.00 * r.tickets + 2.00 * r.tickets;
    const amount = new Money(new Decimal(total));
    this.infra.charge(r.card, amount);
    this.infra.save(r.title + ';' + r.email + ';' + r.tickets + ';' + amount.toString());
    this.infra.sendMail(r.email, 'Bilety ' + r.title + ' x' + r.tickets + ', zaplacono ' + amount.toString());
    return 'OK ' + amount.toString();
  }
}
