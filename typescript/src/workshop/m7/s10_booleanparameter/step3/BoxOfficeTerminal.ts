import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Glasses } from './Glasses.js';
import type { TicketService } from './TicketService.js';

/**
 * Krok 3: klient zmigrowany. customerHasGlasses zostaje booleanem - to dana z formularza,
 * tłumaczymy ją na Glasses na granicy.
 */
export class BoxOfficeTerminal {
  private readonly tickets: TicketService;

  constructor(tickets: TicketService) {
    this.tickets = requireNonNull(tickets, 'tickets');
  }

  sell(title: string, format: string, seats: number, customerHasGlasses: boolean): string {
    const glasses = customerHasGlasses ? Glasses.OWN : Glasses.RENTED;
    return this.tickets.bookAtBoxOffice(title, format, seats, glasses);
  }
}
