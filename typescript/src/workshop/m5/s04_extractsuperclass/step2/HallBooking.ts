import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 2: bez zmian - nadklasa jest gotowa na drugiego potomka.
 */
export abstract class HallBooking {
  readonly #hall: string;
  readonly #start: LocalDateTime;
  readonly #minutes: number;

  protected constructor(hall: string, start: LocalDateTime, minutes: number) {
    this.#hall = hall;
    this.#start = start;
    this.#minutes = minutes;
  }

  hall(): string {
    return this.#hall;
  }

  start(): LocalDateTime {
    return this.#start;
  }

  end(): LocalDateTime {
    return this.#start.plusMinutes(this.#minutes);
  }
}
