import type { Money } from '../../../shared/Money.js';

/**
 * Start: otwarta hierarchia - typowanie strukturalne sprawia, że KAŻDY obiekt o tym kształcie
 * jest biletem (także literał spoza repozytorium), a kalkulator o nim nie wie.
 */
export interface Ticket {
  readonly kind: string;

  basePrice(): Money;
}
