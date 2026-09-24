import type { Screening } from '../Screening.js';
import { TicketPrinter } from './TicketPrinter.js';

/**
 * Klient drukarki: kasa wydająca bilety. Musi znać protokół drukarki
 * (trzy wywołania przed print). Test woła tylko `issue`.
 */
export class TicketDesk {
  private readonly printer = new TicketPrinter();

  issue(screening: Screening, seat: number, buyer: string): string {
    this.printer.selectScreening(screening);
    this.printer.selectSeat(seat);
    this.printer.forBuyer(buyer);
    return this.printer.print();
  }
}
