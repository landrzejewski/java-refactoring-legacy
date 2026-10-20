import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';
import { PaymentService } from './PaymentService.js';

/** Stabilny punkt wejścia sceny (korzeń kompozycji): skąd serwis bierze terminal. Krok 2: bez zmian. */
export class PaymentModule {
  private constructor() {}

  static paymentService(terminal: LegacyTerminalClient): PaymentService {
    return new PaymentService(terminal);
  }
}
