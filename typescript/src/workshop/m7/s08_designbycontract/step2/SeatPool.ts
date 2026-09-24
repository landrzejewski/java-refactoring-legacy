import { Contracts } from './Contracts.js';

/**
 * Krok 2 (rozwiązanie): warunki końcowe (ensure) i niezmiennik 0 <= remaining <= capacity.
 * Niezmiennik sprawdzamy dla nowej wartości PRZED przypisaniem - naruszenie nie zmienia obiektu.
 * Dla poprawnych wejść zachowanie identyczne jak w kroku 1; kontrole chronią przyszłe zmiany.
 */
export class SeatPool {
  private readonly seatCapacity: number;
  private remainingSeats: number;

  constructor(capacity: number) {
    Contracts.require(capacity > 0, 'capacity must be positive');
    this.seatCapacity = capacity;
    this.remainingSeats = capacity;
    this.checkInvariant(this.remainingSeats);
  }

  reserve(seats: number): boolean {
    Contracts.require(seats > 0, 'seats must be positive');
    if (seats > this.remainingSeats) {
      return false;
    }
    const previous = this.remainingSeats;
    const next = previous - seats;
    this.checkInvariant(next);
    this.remainingSeats = next;
    Contracts.ensure(this.remainingSeats === previous - seats, 'reserve must reduce remaining by seats');
    return true;
  }

  release(seats: number): void {
    Contracts.require(seats > 0, 'seats must be positive');
    Contracts.require(seats <= this.seatCapacity - this.remainingSeats, 'cannot release more seats than reserved');
    const previous = this.remainingSeats;
    const next = previous + seats;
    this.checkInvariant(next);
    this.remainingSeats = next;
    Contracts.ensure(this.remainingSeats === previous + seats, 'release must increase remaining by seats');
  }

  remaining(): number {
    return this.remainingSeats;
  }

  capacity(): number {
    return this.seatCapacity;
  }

  private checkInvariant(candidate: number): void {
    Contracts.ensure(candidate >= 0 && candidate <= this.seatCapacity,
      `remaining must stay within 0..${this.seatCapacity}`);
  }
}
