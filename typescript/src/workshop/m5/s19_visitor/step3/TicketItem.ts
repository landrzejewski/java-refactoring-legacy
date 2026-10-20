import type { Money } from '../../../shared/Money.js';

/** Krok 3: bilet (VAT 8%) - czyste dane, bez accept. */
export class TicketItem {
  readonly kind = 'ticket';

  constructor(readonly title: string, readonly format: string, readonly price: Money) {}
}
