import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import { Booking, type BookingLedger } from '../BookingLedger.js';
import type { CinemaApi } from '../CinemaApi.js';

/**
 * Krok 3 (bez zmian): stary system. Od kroku 1 klienci widzą go tylko przez CinemaFacade.
 */
export class LegacyCinema implements CinemaApi {
  constructor(private readonly ledger: BookingLedger) {}

  book(email: string, title: string, format: number, tickets: number, web: boolean): string {
    if (tickets <= 0) {
      return 'ERROR: no seats';
    }
    const p = format === 1 ? 25.00 : format === 2 ? 32.00 : 40.00;
    let sum = p * tickets;
    if (tickets >= 10) {
      sum = sum - sum * 0.10;
    }
    const fees = web ? 2.00 * tickets : 0;
    const id = this.ledger.nextId();
    this.ledger.add(new Booking(id, email, title, tickets,
      new Money(new Decimal(sum)), new Money(new Decimal(fees))));
    return id;
  }

  report(): string {
    const byTitle = new Map<string, number[]>();
    let fees = 0;
    let count = 0;
    for (const b of this.ledger.all()) {
      let row = byTitle.get(b.title);
      if (row === undefined) {
        row = [0, 0];
        byTitle.set(b.title, row);
      }
      row[0] = row[0]! + b.tickets;
      row[1] = row[1]! + b.ticketsValue.amount.toNumber();
      fees = fees + b.fees.amount.toNumber();
      count = count + b.tickets;
    }
    let sb = 'RAPORT\n';
    let revenue = 0;
    for (const title of [...byTitle.keys()].sort()) {
      const row = byTitle.get(title)!;
      sb += title + ': ' + Math.trunc(row[0]!) + ' bil., ' + row[1]!.toFixed(2) + '\n';
      revenue = revenue + row[1]!;
    }
    sb += 'Biletow: ' + count + '\n';
    sb += 'Przychod z biletow: ' + revenue.toFixed(2) + '\n';
    sb += 'Oplaty rezerwacyjne: ' + fees.toFixed(2) + '\n';
    return sb;
  }
}
