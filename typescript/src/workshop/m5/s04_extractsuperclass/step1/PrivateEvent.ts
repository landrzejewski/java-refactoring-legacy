import type { LocalDateTime } from '../../../shared/time.js';

/** Krok 1: bez zmian - dołączamy klasy pojedynczo, PrivateEvent w następnym kroku. */
export class PrivateEvent {
  readonly #client: string;
  readonly #hall: string;
  readonly #start: LocalDateTime;
  readonly #minutes: number;

  constructor(client: string, hall: string, start: LocalDateTime, minutes: number) {
    this.#client = client;
    this.#hall = hall;
    this.#start = start;
    this.#minutes = minutes;
  }

  /** Fabryka: standardowy wynajem trwa 2 godziny. */
  static rental(client: string, hall: string, start: LocalDateTime): PrivateEvent {
    return new PrivateEvent(client, hall, start, 120);
  }

  client(): string {
    return this.#client;
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
