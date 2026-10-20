import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import { ReservationController } from './adapter/ReservationController.js';

/**
 * Punkt startowy aplikacji. Na razie tylko tworzy kontroler - graf obiektów składa
 * sam kontroler. W kroku 4 ten moduł stanie się prawdziwym composition root.
 * Test woła tylko tę funkcję, więc refaktoryzacja na żywo go nie psuje.
 */
export function reservationController(db: RowStore, outbox: Outbox): ReservationController {
  return new ReservationController(db, outbox);
}
