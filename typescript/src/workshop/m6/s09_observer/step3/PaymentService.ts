import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Payment } from '../Payment.js';
import type { PaymentListener } from './PaymentListener.js';
import { ReservationPaid } from './ReservationPaid.js';
import type { Subscription } from './Subscription.js';

/** Osobny obiekt na każde subscribe - ten sam obiekt słuchacza zapisany dwa razy to dwie rejestracje. */
class Registration {
  constructor(readonly listener: PaymentListener) {}
}

/**
 * Krok 3: subject z subscribe - serwis nie zna już żadnego konkretnego odbiorcy.
 * Kontrakt: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce listy.
 */
export class PaymentService {
  private listeners: readonly Registration[] = [];
  private readonly paidList: string[] = [];

  subscribe(listener: PaymentListener): Subscription {
    const registration = new Registration(requireNonNull(listener, 'listener'));
    // kopia przy zapisie (w Javie: CopyOnWriteArrayList) - trwająca pętla widzi starą migawkę
    this.listeners = [...this.listeners, registration];
    let active = true;
    return {
      close: () => {
        if (active) {
          active = false;
          this.listeners = this.listeners.filter((r) => r !== registration);
        }
      },
    };
  }

  confirm(payment: Payment): void {
    if (payment.amount.amount.lte(0)) {
      throw new IllegalArgumentError('amount must be positive');
    }
    this.paidList.push(payment.reservationId);
    const event = new ReservationPaid(
      payment.reservationId, payment.email, payment.phone, payment.amount);
    for (const registration of this.listeners) {
      registration.listener.onPaid(event);
    }
  }

  paid(): readonly string[] {
    return Object.freeze([...this.paidList]);
  }
}
