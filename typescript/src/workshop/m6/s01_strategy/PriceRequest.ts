import type { Money } from '../../shared/Money.js';

/**
 * Stabilny kontrakt sceny: cena bazowa formatu, typ biletu (legacy: N, S, E, C)
 * i nazwa programu zniżek skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE).
 */
export class PriceRequest {
  constructor(
    readonly base: Money,
    readonly ticketType: string,
    readonly program: string | null,
  ) {}
}
