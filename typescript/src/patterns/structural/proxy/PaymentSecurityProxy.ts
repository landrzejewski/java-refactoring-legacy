import { JulLogger } from '../../JulLogger.js';
import type { PaymentsService } from './PaymentsService.js';

export class PaymentSecurityProxy implements PaymentsService {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.structural.proxy.PaymentSecurityProxy');

  constructor(private readonly paymentsService: PaymentsService) {}

  pay(properties: ReadonlyMap<string, string>): void {
    PaymentSecurityProxy.log.info('Checking security', 'pay');
    this.paymentsService.pay(properties);
  }
}
