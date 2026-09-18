import { JulLogger } from '../../JulLogger.js';
import type { PaymentsService } from './PaymentsService.js';

export class PaymentLoggerProxy implements PaymentsService {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.structural.proxy.PaymentLoggerProxy');

  constructor(private readonly paymentsService: PaymentsService) {}

  pay(properties: ReadonlyMap<string, string>): void {
    this.paymentsService.pay(properties);
    PaymentLoggerProxy.log.info('Payment completed', 'pay');
  }
}
