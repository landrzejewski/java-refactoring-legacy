import { assertNever } from '../../../../shared/assertNever.js';
import type { BookingRequest } from '../BookingRequest.js';
import { LegacyTicketPricing } from './LegacyTicketPricing.js';
import { ModernTicketPricing } from './ModernTicketPricing.js';
import { PricingMode } from './PricingMode.js';
import type { TicketPricing } from './TicketPricing.js';

/**
 * Krok 3: przełącznik w jednym miejscu. Domyślnie LEGACY - wdrożenie nowego kodu
 * nie zmienia zachowania, dopiero konfiguracja (MODERN) je przełącza.
 */
export class BookingService {
  private readonly pricing: TicketPricing;

  constructor(modeOrPricing: PricingMode | TicketPricing = PricingMode.LEGACY) {
    this.pricing = typeof modeOrPricing === 'string' ? BookingService.pricingFor(modeOrPricing) : modeOrPricing;
  }

  private static pricingFor(mode: PricingMode): TicketPricing {
    switch (mode) {
      case PricingMode.LEGACY: return new LegacyTicketPricing();
      case PricingMode.MODERN: return new ModernTicketPricing();
      default: return assertNever(mode);
    }
  }

  confirm(request: BookingRequest): string {
    return request.screening.title + ': ' + request.seats.join(',')
      + ' - do zaplaty ' + this.pricing.total(request).toString();
  }
}
