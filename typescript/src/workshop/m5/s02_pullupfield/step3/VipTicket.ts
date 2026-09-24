import { Ticket } from './Ticket.js';

/** Krok 3: reguła normalizacji VIP zostaje w podklasie - przekazuje do bazy już znormalizowaną wartość. */
export class VipTicket extends Ticket {
  constructor(seat: string) {
    super(seat.toUpperCase());
  }

  override describe(): string {
    return 'VIP ' + this.seat();
  }
}
