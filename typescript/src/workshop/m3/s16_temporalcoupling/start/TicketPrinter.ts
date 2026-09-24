import type { Screening } from '../Screening.js';

/**
 * Start: sprzężenie czasowe (protokół wywołań). Żeby wydrukować bilet, trzeba najpierw
 * wywołać selectScreening, selectSeat i forBuyer - w dowolnej kolejności, ale wszystkie.
 * Nic w typach tego nie mówi (pola są opcjonalne, a print je "odpakowuje" przez !):
 * zapomniane wywołanie to TypeError w print, a obiekt współdzielony przez dwie kasy
 * miesza dane klientów.
 */
export class TicketPrinter {
  private screening: Screening | undefined;
  private seat: number | undefined;
  private buyer: string | undefined;

  selectScreening(screening: Screening): void {
    this.screening = screening;
  }

  selectSeat(seat: number): void {
    this.seat = seat;
  }

  forBuyer(email: string): void {
    this.buyer = email;
  }

  print(): string {
    return `BILET ${this.screening!.title} (${this.screening!.format}) ${this.screening!.start.toString()}`
      + `, miejsce ${this.seat!.toFixed(0)}, dla ${this.buyer!.toLowerCase()}`;
  }
}
