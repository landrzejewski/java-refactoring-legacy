import type { ReservationPaid } from './ReservationPaid.js';

/** Krok 2: kontrakt obserwatora - subject zna tylko ten interfejs. Wywołanie synchroniczne. */
export interface PaymentListener {
  onPaid(event: ReservationPaid): void;
}
