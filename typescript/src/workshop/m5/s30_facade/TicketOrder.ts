import { requireNonNull } from '../../../shared/requireNonNull.js';

/** Stabilny kontrakt sceny: zamówienie biletów jednego typu na kilka miejsc jednego seansu. */
export class TicketOrder {
  readonly screening: string;
  readonly seats: readonly string[];
  readonly ticketType: string;

  constructor(screening: string, seats: readonly string[], ticketType: string,
    readonly card: string, readonly email: string) {
    this.screening = requireNonNull(screening, 'screening');
    this.seats = Object.freeze([...seats]);
    this.ticketType = requireNonNull(ticketType, 'ticketType');
  }
}
