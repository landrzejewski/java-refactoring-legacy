import type { Screening } from '../Screening.js';
import { TicketPrinter } from './TicketPrinter.js';

/** Krok 1: kasa po Change Signature - jedno wywołanie, bez protokołu. */
export class TicketDesk {
  private readonly printer = new TicketPrinter();

  issue(screening: Screening, seat: number, buyer: string): string {
    return this.printer.print(screening, seat, buyer);
  }
}
