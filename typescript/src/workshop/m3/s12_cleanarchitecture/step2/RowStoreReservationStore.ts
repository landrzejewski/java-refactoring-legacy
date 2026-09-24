import type { RowStore } from '../RowStore.js';
import type { NewReservation } from './NewReservation.js';
import type { ReservationStore } from './ReservationStore.js';

/** Krok 2: adapter wyjściowy - mapuje rekord na kolumny tabeli. */
export class RowStoreReservationStore implements ReservationStore {
  constructor(private readonly db: RowStore) {}

  save(reservation: NewReservation): string {
    return this.db.insert([
      reservation.email, reservation.format, reservation.seats, reservation.total]);
  }
}
