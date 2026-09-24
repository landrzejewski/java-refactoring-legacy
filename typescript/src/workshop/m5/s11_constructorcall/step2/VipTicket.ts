import { Ticket } from './Ticket.js';

/** Krok 2: zwykły konstruktor, bez nadpisanego label() - poprawność nie zależy już od kolejności inicjalizacji. */
export class VipTicket extends Ticket {
  private readonly lounge: string;

  constructor(seat: string, lounge: string) {
    super(seat);
    this.lounge = lounge;
  }

  protected override describe(): string {
    return super.describe() + ' (VIP: ' + this.lounge + ')';
  }
}
