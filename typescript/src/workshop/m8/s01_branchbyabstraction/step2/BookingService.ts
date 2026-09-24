import type { BookingRequest } from '../BookingRequest.js';
import { LegacyTicketPricing } from './LegacyTicketPricing.js';
import type { TicketPricing } from './TicketPricing.js';

/** Krok 2: klient zależy od abstrakcji TicketPricing; domyślnie działa stara implementacja. */
export class BookingService {
  constructor(private readonly pricing: TicketPricing = new LegacyTicketPricing()) {}

  confirm(request: BookingRequest): string {
    return request.screening.title + ': ' + request.seats.join(',')
      + ' - do zaplaty ' + this.pricing.total(request).toString();
  }
}
