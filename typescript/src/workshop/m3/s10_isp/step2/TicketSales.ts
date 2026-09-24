/** Rola z perspektywy kasy. */
export interface TicketSales {
  sellTicket(title: string, seat: number): string;

  refundTicket(ticketId: string): string;
}
