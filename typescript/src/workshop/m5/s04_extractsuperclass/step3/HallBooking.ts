import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 3 (rozwiązanie): warunek kolizji wyciągnięty (Extract Method) i przeniesiony do nadklasy
 * jako `overlaps` (w Javie final); `name()` to abstrakcyjny punkt rozszerzenia dla opisu konfliktu.
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

  abstract name(): string;

  overlaps(other: HallBooking): boolean {
    return this.#hall === other.#hall && this.#start.isBefore(other.end()) && other.#start.isBefore(this.end());
  }
}
