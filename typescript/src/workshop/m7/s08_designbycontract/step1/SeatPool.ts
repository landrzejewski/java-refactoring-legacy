import { Contracts } from './Contracts.js';

/**
 * Krok 1: warunki wstępne (preconditions) przed pierwszą mutacją. To NIE jest refaktoryzacja:
 * dla niepoprawnych wejść zachowanie się zmienia (wyjątek zamiast cichego zepsucia stanu).
 * Dla poprawnych wejść - bez zmian, łącznie z "false" przy braku miejsc.
 */
export class SeatPool {
  private readonly seatCapacity: number;
  private remainingSeats: number;

  constructor(capacity: number) {
    Contracts.require(capacity > 0, 'capacity must be positive');
    this.seatCapacity = capacity;
    this.remainingSeats = capacity;
  }

  reserve(seats: number): boolean {
    Contracts.require(seats > 0, 'seats must be positive');
    if (seats > this.remainingSeats) {
      return false;
    }
    this.remainingSeats = this.remainingSeats - seats;
    return true;
  }

  release(seats: number): void {
    Contracts.require(seats > 0, 'seats must be positive');
    Contracts.require(seats <= this.seatCapacity - this.remainingSeats, 'cannot release more seats than reserved');
    this.remainingSeats = this.remainingSeats + seats;
  }

  remaining(): number {
    return this.remainingSeats;
  }

  capacity(): number {
    return this.seatCapacity;
  }
}
