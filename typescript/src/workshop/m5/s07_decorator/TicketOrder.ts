import type { Money } from '../../shared/Money.js';

/**
 * Stabilny kontrakt sceny: zamówienie biletu z dodatkami. VIP +10.00, okulary +3.00 dla 3D
 * (chyba że klient ma własne), ubezpieczenie biletu +4.00 (wartość przykładowa).
 */
export class TicketOrder {
  constructor(
    readonly title: string,
    readonly format: string,
    readonly base: Money,
    readonly vip: boolean,
    readonly ownGlasses: boolean,
    readonly insurance: boolean,
  ) {}
}
