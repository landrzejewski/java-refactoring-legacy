import type { Decimal } from 'decimal.js';

/**
 * Stabilny kontrakt sceny - sprzedaż na jeden seans (kwoty brutto).
 *
 * @param tickets       liczba sprzedanych biletów
 * @param ticketRevenue przychód z biletów (VAT 8%)
 * @param barRevenue    przychód z baru (VAT 23%)
 */
export class Sale {
  constructor(
    readonly title: string,
    readonly tickets: number,
    readonly ticketRevenue: Decimal,
    readonly barRevenue: Decimal,
  ) {}
}
