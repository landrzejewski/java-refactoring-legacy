import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import { BookSeats } from './BookSeats.js';
import { BookSeatsCommand } from './BookSeatsCommand.js';

/**
 * Krok 1: kontroler tylko tłumaczy - parametry na `BookSeatsCommand`,
 * wynik i wyjątki na kody odpowiedzi.
 */
export class ReservationController {
  private readonly bookSeats: BookSeats;

  constructor(db: RowStore, outbox: Outbox) {
    this.bookSeats = new BookSeats(db, outbox);
  }

  handle(params: ReadonlyMap<string, string>): string {
    const email = params.get('email');
    if (email === undefined || email.trim() === '') {
      return '400 brak email';
    }
    const rows = (params.get('rows') ?? '').split(',')
      .filter((s) => s.trim() !== '').map((s) => Number.parseInt(s, 10));
    try {
      const booking = this.bookSeats.execute(
        new BookSeatsCommand(email, params.get('format') ?? '2D', rows));
      return `201 ${booking.id} ${booking.total.toString()}`;
    } catch (error) {
      if (error instanceof IllegalArgumentError) {
        return `400 ${error.message}`;
      }
      if (error instanceof IllegalStateError) {
        return `503 ${error.message}`;
      }
      throw error;
    }
  }
}
