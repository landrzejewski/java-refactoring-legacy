import type { Money } from '../../shared/Money.js';
import type { LocalDateTime } from '../../shared/time.js';

/** Stabilny kontrakt sceny: zapłacona cena biletu i start seansu. */
export class TicketData {
  constructor(readonly price: Money, readonly showStart: LocalDateTime) {}
}
