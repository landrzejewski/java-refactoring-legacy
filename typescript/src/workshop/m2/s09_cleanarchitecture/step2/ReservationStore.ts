import type { NewReservation } from './NewReservation.js';

/**
 * Krok 2: port wyjściowy zdefiniowany przez potrzebę przypadku użycia.
 * Zwraca identyfikator; gdy nie da się zapisać - `IllegalStateError`.
 */
export interface ReservationStore {
  save(reservation: NewReservation): string;
}
