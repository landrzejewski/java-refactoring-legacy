import type { NewReservation } from './NewReservation.js';

/** Krok 2: port wyjściowy - "daj znać, że rezerwacja powstała". */
export interface BookingNotifier {
  reservationCreated(id: string, reservation: NewReservation): void;
}
