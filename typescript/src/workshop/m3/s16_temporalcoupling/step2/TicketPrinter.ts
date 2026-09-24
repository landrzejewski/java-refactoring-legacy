import type { TicketRequest } from './TicketRequest.js';

/** Krok 2 (rozwiązanie): drukarka dostaje kompletny `TicketRequest`. */
export class TicketPrinter {
  print(request: TicketRequest): string {
    const screening = request.screening;
    return `BILET ${screening.title} (${screening.format}) ${screening.start.toString()}`
      + `, miejsce ${request.seat}, dla ${request.buyer.toLowerCase()}`;
  }
}
