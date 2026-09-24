import type { Money } from '../../../shared/Money.js';
import type { PaymentResult } from '../PaymentResult.js';

/** Krok 3: preferowany interfejs kina - kwota jako Money, odmowa jako wynik, nie wyjątek. */
export interface PaymentGateway {
  pay(reservationId: string, amount: Money): PaymentResult;
}
