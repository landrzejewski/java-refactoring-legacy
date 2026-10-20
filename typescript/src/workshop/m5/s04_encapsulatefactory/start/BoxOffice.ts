import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './tickets/StandardTicket.js';
import type { Ticket } from './tickets/Ticket.js';
import { VipTicket } from './tickets/VipTicket.js';

/**
 * Start: klient zna klasy konkretne biletów i regułę VIP (rząd 10+), tworzy je przez new
 * w dwóch miejscach. Nowy rodzaj biletu = zmiany u każdego klienta.
 */
export class BoxOffice {
  sell(title: string, base: Money, row: number): Ticket {
    if (row >= 10) {
      return new VipTicket(title, base, row);
    }
    return new StandardTicket(title, base, row);
  }

  sellAll(title: string, base: Money, rows: readonly number[]): Ticket[] {
    const tickets: Ticket[] = [];
    for (const row of rows) {
      tickets.push(row >= 10
        ? new VipTicket(title, base, row)
        : new StandardTicket(title, base, row));
    }
    return tickets;
  }
}
