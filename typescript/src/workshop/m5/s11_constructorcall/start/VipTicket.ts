import { Ticket } from './Ticket.js';

/**
 * Start: pole lounge jest jeszcze undefined, gdy baza woła describe() - etykieta zawiera "undefined"
 * na zawsze. (Gdyby pole było prywatne ES - #lounge - odczyt skończyłby się TypeError.)
 */
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
