import { PaymentLoggerProxy } from './PaymentLoggerProxy.js';
import { PaymentSecurityProxy } from './PaymentSecurityProxy.js';
import { RemotePaymentsService } from './RemotePaymentsService.js';

export function run(): void {
  const paymentsService = new PaymentLoggerProxy(new PaymentSecurityProxy(new RemotePaymentsService()));
  //----------------------------------------------------------------
  paymentsService.pay(new Map()); // Java: emptyMap()
}
