import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './tickets/Ticket.js';
import { Tickets } from './tickets/Tickets.js';

/**
 * Krok 1: każde new zastąpione wywołaniem metody tworzącej. Klient nie importuje już
 * klas konkretnych, ale nadal zna regułę VIP.
 */
export class BoxOffice {
  sell(title: string, base: Money, row: number): Ticket {
    if (row >= 10) {
      return Tickets.vip(title, base, row);
    }
    return Tickets.standard(title, base, row);
  }

  sellAll(title: string, base: Money, rows: readonly number[]): Ticket[] {
    const tickets: Ticket[] = [];
    for (const row of rows) {
      tickets.push(row >= 10 ? Tickets.vip(title, base, row) : Tickets.standard(title, base, row));
    }
    return tickets;
  }
}
