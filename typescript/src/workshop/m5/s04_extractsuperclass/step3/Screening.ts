import type { LocalDateTime } from '../../../shared/time.js';
import { HallBooking } from './HallBooking.js';

/** Krok 3: Screening podaje tylko swoją nazwę do opisu konfliktu. */
export class Screening extends HallBooking {
  readonly #title: string;

  constructor(title: string, hall: string, start: LocalDateTime, minutes: number) {
    super(hall, start, minutes);
    this.#title = title;
  }

  title(): string {
    return this.#title;
  }

  override name(): string {
    return this.#title;
  }
}
