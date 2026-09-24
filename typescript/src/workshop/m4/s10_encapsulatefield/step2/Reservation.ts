/**
 * Krok 2: setter zastąpiony operacjami domenowymi (Move Method z BoxOffice + Remove Setting Method).
 * Warunki przepisane 1:1 - niedozwolone przejście nadal jest cicho ignorowane.
 * Wejście gościa ma teraz uczciwą nazwę zamiast anonimowego `status = 'USED'`.
 */
export class Reservation {
  #status = 'NEW';

  get status(): string {
    return this.#status;
  }

  pay(): void {
    if (this.#status === 'NEW') {
      this.#status = 'PAID';
    }
  }

  checkIn(): void {
    if (this.#status === 'PAID') {
      this.#status = 'USED';
    }
  }

  cancel(): void {
    if (this.#status === 'NEW' || this.#status === 'PAID') {
      this.#status = 'CANCELLED';
    }
  }

  expire(): void {
    if (this.#status === 'NEW') {
      this.#status = 'EXPIRED';
    }
  }

  admitGuestWithoutPayment(): void {
    this.#status = 'USED';
  }
}
