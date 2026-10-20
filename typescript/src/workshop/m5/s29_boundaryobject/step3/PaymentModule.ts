import type { LegacyTerminalClient } from '../LegacyTerminalClient.js';
import { LegacyTerminalAdapter } from './LegacyTerminalAdapter.js';
import { PaymentService } from './PaymentService.js';

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: tu, i tylko tu, terminal dostaje adapter. */
export class PaymentModule {
  private constructor() {}

  static paymentService(terminal: LegacyTerminalClient): PaymentService {
    return new PaymentService(new LegacyTerminalAdapter(terminal));
  }
}
