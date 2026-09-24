import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import { OutboxBookingNotifier } from './adapter/OutboxBookingNotifier.js';
import { ReservationController } from './adapter/ReservationController.js';
import { RowStoreReservationStore } from './adapter/RowStoreReservationStore.js';
import { BookSeats } from './app/BookSeats.js';

/**
 * Krok 4 (rozwiązanie): composition root - jedyne miejsce, które zna wszystkie konkrety
 * i składa graf ręcznymi konstruktorami. Nie zawiera reguł biznesowych.
 * Podmiana adaptera (np. inna baza) = zmiana tylko tutaj.
 */
export function reservationController(db: RowStore, outbox: Outbox): ReservationController {
  const bookSeats = new BookSeats(
    new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
  return new ReservationController(bookSeats);
}
