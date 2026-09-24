import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 1: Extract Superclass z klasy Screening. Nazwa opisuje pojęcie domenowe (rezerwacja sali),
 * a nie motywację ("BaseScreening", "AbstractCommon"). Pola prywatne, ustawiane przez super(...).
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
