import { IllegalArgumentError, IllegalStateError } from '../../../../../shared/errors.js';
import type { BookSeats } from '../app/BookSeats.js';
import { BookSeatsCommand } from '../app/BookSeatsCommand.js';

/**
 * Krok 4: adapter wejściowy dostaje gotowy przypadek użycia - nie wie, jakie
 * adaptery wyjściowe stoją za portami. Tylko tłumaczy żądanie i odpowiedź.
 */
export class ReservationController {
  constructor(private readonly bookSeats: BookSeats) {}

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
