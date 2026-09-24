import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 3: rezerwacja jako typ zamiast unknown[] [screeningId, email, phone, seats, types, web,
 * total, status, createdAt, card, ticketsSum]. Status zostaje kodem liczbowym jak w legacy -
 * zamiana na enum to kolejny, osobny krok.
 * Eksportowany tylko na potrzeby klas kroku (w Javie klasa pakietowa).
 */
export class Booking {
  // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
  status = 0;
  private paidWith: string | null = null;

  constructor(
    readonly id: string,
    readonly screeningId: string,
    readonly email: string,
    readonly phone: string | null,
    readonly seats: string[],
    readonly types: string[],
    readonly web: boolean,
    readonly total: number,
    readonly createdAt: LocalDateTime,
    readonly ticketsSum: number,
  ) {}

  get card(): string | null {
    return this.paidWith;
  }

  markPaid(card: string | null): void {
    this.status = 1;
    this.paidWith = card;
  }
}
