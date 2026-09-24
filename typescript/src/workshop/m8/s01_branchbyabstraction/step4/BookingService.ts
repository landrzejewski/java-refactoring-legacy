import type { BookingRequest } from '../BookingRequest.js';
import { ModernTicketPricing } from './ModernTicketPricing.js';
import type { TicketPricing } from './TicketPricing.js';

/**
 * Krok 4: usunięcie starej ścieżki. Po okresie obserwacji w trybie MODERN kasujemy
 * LegacyTicketPricing i PricingMode - migracja jest zamknięta dopiero teraz.
 */
export class BookingService {
  constructor(private readonly pricing: TicketPricing = new ModernTicketPricing()) {}

  confirm(request: BookingRequest): string {
    return request.screening.title + ': ' + request.seats.join(',')
      + ' - do zaplaty ' + this.pricing.total(request).toString();
  }
}
