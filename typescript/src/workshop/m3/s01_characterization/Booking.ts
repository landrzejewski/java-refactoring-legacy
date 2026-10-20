import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny - wspólny dla start i wszystkich kroków.
 *
 * @param format      legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param ticketTypes legacy kody biletów: "N" normalny, "S" student, "E" senior, "C" dziecko
 * @param online      rezerwacja przez internet (opłata rezerwacyjna) albo w kasie
 */
export class Booking {
  constructor(
    readonly customer: string,
    readonly title: string,
    readonly format: number,
    readonly start: LocalTime,
    readonly ticketTypes: readonly string[],
    readonly online: boolean,
  ) {}
}
