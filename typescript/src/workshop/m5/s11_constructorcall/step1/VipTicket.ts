import { Ticket } from './Ticket.js';

/**
 * Krok 1: szybka naprawa lokalna. W JS nie ma prologu konstruktora z Javy 25 (JEP 513) - `this`
 * przed super(...) to błąd - więc podklasa nadpisuje label() i liczy etykietę na żądanie, omijając
 * zapamiętaną w bazie. Działa, ale kruche: każda nowa podklasa musi o tym pamiętać, a baza dalej
 * woła describe() z konstruktora.
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

  override label(): string {
    return this.describe();
  }
}
