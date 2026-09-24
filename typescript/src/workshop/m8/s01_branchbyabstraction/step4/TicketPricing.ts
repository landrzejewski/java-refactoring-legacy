import type { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';

/**
 * Krok 4: abstrakcja zostaje jako szew dla testów i kolejnych zmian.
 * Stara implementacja i przełącznik zostały usunięte (Safe Delete).
 */
export interface TicketPricing {
  total(request: BookingRequest): Money;
}
