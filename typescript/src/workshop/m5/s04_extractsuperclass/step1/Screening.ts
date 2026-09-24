import type { LocalDateTime } from '../../../shared/time.js';
import { HallBooking } from './HallBooking.js';

/** Krok 1: Screening dołączony do HallBooking - publiczny konstruktor bez zmian, woła super(...). */
export class Screening extends HallBooking {
  readonly #title: string;

  constructor(title: string, hall: string, start: LocalDateTime, minutes: number) {
    super(hall, start, minutes);
    this.#title = title;
  }

  title(): string {
    return this.#title;
  }
}
