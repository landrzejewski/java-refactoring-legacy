import type { Money } from '../../../shared/Money.js';
import type { PaymentConfirmation } from './PaymentConfirmation.js';

/** Krok 3: port domeny - terminal płatniczy widziany oczami kina: Money na wejściu, obiekt graniczny na wyjściu. */
export interface PaymentTerminal {
  authorize(bookingId: string, amount: Money): PaymentConfirmation;
}
