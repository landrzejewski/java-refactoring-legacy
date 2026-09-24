import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './tickets/Ticket.js';
import { Tickets } from './tickets/Tickets.js';

/** Krok 3: klient bez zmian - zmieniła się tylko widoczność klas biletów. */
export class BoxOffice {
  sell(title: string, base: Money, row: number): Ticket {
    return Tickets.forSeat(title, base, row);
  }

  sellAll(title: string, base: Money, rows: readonly number[]): Ticket[] {
    const tickets: Ticket[] = [];
    for (const row of rows) {
      tickets.push(this.sell(title, base, row));
    }
    return tickets;
  }
}
