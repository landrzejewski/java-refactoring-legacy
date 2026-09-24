import type { Decimal } from 'decimal.js';

import type { Deal } from '../Deal.js';

/**
 * Krok 1: Strategy - wspólny kontrakt modeli rozliczeń.
 * Wynik: kwota dla dystrybutora, skala 2, nieujemna, bez efektów ubocznych.
 */
export interface SettlementModel {
  payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal;
}
