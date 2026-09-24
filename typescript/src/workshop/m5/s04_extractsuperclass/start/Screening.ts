import type { LocalDateTime } from '../../../shared/time.js';

/** Start: seans - sala, początek, czas trwania i koniec. PrivateEvent ma to samo, osobno. */
export class Screening {
  readonly #title: string;
  readonly #hall: string;
  readonly #start: LocalDateTime;
  readonly #minutes: number;

  constructor(title: string, hall: string, start: LocalDateTime, minutes: number) {
    this.#title = title;
    this.#hall = hall;
    this.#start = start;
    this.#minutes = minutes;
  }

  title(): string {
    return this.#title;
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
