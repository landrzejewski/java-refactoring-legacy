import type { Money } from '../../shared/Money.js';

/** Stabilny kontrakt sceny: bilet z ceną bazową formatu. */
export interface Ticket {
  basePrice(): Money;
}
