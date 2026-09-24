import type { Money } from '../../../shared/Money.js';
import type { BookingRequest } from '../BookingRequest.js';

/**
 * Krok 2: abstrakcja - kontrakt projektowany pod przyszłą implementację (Money, nie double).
 * To jest "gałąź" w Branch by Abstraction: w kodzie, nie w systemie kontroli wersji.
 */
export interface TicketPricing {
  total(request: BookingRequest): Money;
}
