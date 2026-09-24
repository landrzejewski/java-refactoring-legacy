import { Ticket } from './Ticket.js';

/** Krok 3: stan miejsca przeniesiony do bazy, podklasa tylko go opisuje. */
export class StandardTicket extends Ticket {
  constructor(seat: string) {
    super(seat);
  }

  override describe(): string {
    return 'NORMAL ' + this.seat();
  }
}
