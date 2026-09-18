import { Decimal } from 'decimal.js';
import { DiscountPolicy } from './DiscountPolicy.js';
import {
  LegacyOrderService,
  Order,
  OrderLine,
  type MailGateway,
  type OrderRepository,
} from './LegacyOrderService.js';
import { Item, OrderSummary, RiskClassifier } from './RiskClassifier.js';
import { SalesCalculations } from './SalesCalculations.js';

export class Module1Examples {
  private constructor() {}

  static main(_args: readonly string[] = []): void {
    Module1Examples.runLegacyOrderService();
    Module1Examples.runRiskClassifier();
    Module1Examples.runDiscountPolicy();
    Module1Examples.runSalesCalculations();
  }

  private static runLegacyOrderService(): void {
    const repository: OrderRepository = {
      save: (orderId, total) => console.log(`Saved order ${orderId} with total ${total.toFixed(2)}`),
    };
    const mailGateway: MailGateway = {
      send: (recipient, body) => console.log(`Sent to ${recipient}: ${body}`),
    };

    const service = new LegacyOrderService(repository, mailGateway);
    const order = new Order(
      '9aa026a4-fc39-4af8-a008-d9b831b0ba59',
      'customer@example.com',
      [new OrderLine('BOOK-1', 2, new Decimal('100.00'))],
    );

    const receipt = service.placeOrder(order, 'VIP', false, 'PL');
    console.log('Receipt: ' + receipt.toString());
  }

  private static runRiskClassifier(): void {
    const order = new OrderSummary(
      new Decimal('1500.00'),
      true,
      [new Item(true), new Item(false)],
    );

    console.log('Risk level: ' + RiskClassifier.riskLevel(order));
  }

  private static runDiscountPolicy(): void {
    const discount = DiscountPolicy.discountPercent(100, true);
    console.log('Discount: ' + discount + '%');
  }

  private static runSalesCalculations(): void {
    const price = new Decimal('100.00');
    const invoice = SalesCalculations.invoiceLineTotal(price, 1, true);
    const quote = SalesCalculations.quoteLineTotal(price, 1, true);

    // Java BigDecimal zachowuje skalę mnożenia (2 + 2 = 4) i drukuje "90.0000";
    // decimal.js nie przechowuje skali, więc odtwarzamy ją jawnie.
    console.log('Invoice line total: ' + invoice.toFixed(4));
    console.log('Quote line total: ' + quote.toFixed(4));
  }
}
