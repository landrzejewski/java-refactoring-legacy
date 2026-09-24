import type { LocalDateTime } from '../../../shared/time.js';
import { HallBooking } from './HallBooking.js';

/**
 * Krok 2: PrivateEvent extends HallBooking - zduplikowane pola i akcesory usunięte.
 * Konstruktor i fabryka rental(...) zachowane: bez własnego konstruktora podklasa dziedziczyłaby
 * chroniony konstruktor bazy (hall, start, minutes) - publiczną sygnaturę pilnujemy sami.
 */
export class PrivateEvent extends HallBooking {
  readonly #client: string;

  constructor(client: string, hall: string, start: LocalDateTime, minutes: number) {
    super(hall, start, minutes);
    this.#client = client;
  }

  /** Fabryka: standardowy wynajem trwa 2 godziny. */
  static rental(client: string, hall: string, start: LocalDateTime): PrivateEvent {
    return new PrivateEvent(client, hall, start, 120);
  }

  client(): string {
    return this.#client;
  }
}
