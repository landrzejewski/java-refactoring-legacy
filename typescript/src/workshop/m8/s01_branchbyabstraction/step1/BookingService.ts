import type { BookingRequest } from '../BookingRequest.js';
import { LegacyTicketPricing } from './LegacyTicketPricing.js';

/** Krok 1: potwierdzenie deleguje wyliczenie ceny do wydzielonej klasy LegacyTicketPricing. */
export class BookingService {
  private readonly pricing = new LegacyTicketPricing();

  confirm(request: BookingRequest): string {
    const total = this.pricing.total(request);
    return request.screening.title + ': ' + request.seats.join(',')
      + ' - do zaplaty ' + total.toFixed(2);
  }
}
