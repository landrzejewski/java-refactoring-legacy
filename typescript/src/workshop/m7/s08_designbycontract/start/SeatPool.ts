/**
 * Start: pula miejsc seansu bez żadnych kontraktów. reserve(-2) po cichu "dodaje" miejsca,
 * a release() pozwala przekroczyć pojemność sali. Stan może stać się niemożliwy.
 */
export class SeatPool {
  private readonly seatCapacity: number;
  private remainingSeats: number;

  constructor(capacity: number) {
    this.seatCapacity = capacity;
    this.remainingSeats = capacity;
  }

  /** Zwraca false, gdy wolnych miejsc jest za mało - to poprawna, udokumentowana odpowiedź. */
  reserve(seats: number): boolean {
    if (seats > this.remainingSeats) {
      return false;
    }
    this.remainingSeats = this.remainingSeats - seats;
    return true;
  }

  release(seats: number): void {
    this.remainingSeats = this.remainingSeats + seats;
  }

  remaining(): number {
    return this.remainingSeats;
  }

  capacity(): number {
    return this.seatCapacity;
  }
}
