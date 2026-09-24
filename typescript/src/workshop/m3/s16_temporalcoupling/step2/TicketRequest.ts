import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Screening } from '../Screening.js';

/**
 * Krok 2: Introduce Parameter Object - dane biletu jako jedna wartość,
 * kompletna od chwili utworzenia (null/undefined odrzucony od razu, a nie w print).
 */
export class TicketRequest {
  readonly screening: Screening;
  readonly buyer: string;

  constructor(screening: Screening, readonly seat: number, buyer: string) {
    this.screening = requireNonNull(screening, 'screening');
    this.buyer = requireNonNull(buyer, 'buyer');
  }
}
