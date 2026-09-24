import type { Screening } from '../Screening.js';
import { TicketPrinter } from './TicketPrinter.js';
import { TicketRequest } from './TicketRequest.js';

/** Krok 2: kasa buduje kompletne żądanie biletu. */
export class TicketDesk {
  private readonly printer = new TicketPrinter();

  issue(screening: Screening, seat: number, buyer: string): string {
    return this.printer.print(new TicketRequest(screening, seat, buyer));
  }
}
