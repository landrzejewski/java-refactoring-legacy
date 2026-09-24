import type { Money } from '../../shared/Money.js';

/** Stabilny kontrakt sceny: opłata za rezerwację (kwota biletów, bez opłat). */
export class Payment {
  constructor(
    readonly reservationId: string,
    readonly email: string,
    readonly phone: string,
    readonly amount: Money,
  ) {}
}
