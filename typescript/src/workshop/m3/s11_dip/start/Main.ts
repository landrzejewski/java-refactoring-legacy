import { ConfirmReservation } from './app/ConfirmReservation.js';

/**
 * Composition root wariantu: jedyne miejsce, które składa graf obiektów.
 * Test woła tylko tę funkcję, więc refaktoryzacja konstruktorów na żywo go nie psuje
 * (Introduce Parameter w IDE sam przeniesie tu tworzenie zależności).
 */
export function confirmReservation(): ConfirmReservation {
  return new ConfirmReservation();
}
