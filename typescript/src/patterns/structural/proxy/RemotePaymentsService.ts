import { JulLogger } from '../../JulLogger.js';
import type { PaymentsService } from './PaymentsService.js';

export class RemotePaymentsService implements PaymentsService {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.structural.proxy.RemotePaymentsService');

  pay(_properties: ReadonlyMap<string, string>): void {
    RemotePaymentsService.log.info('Payment started', 'pay');
  }
}
