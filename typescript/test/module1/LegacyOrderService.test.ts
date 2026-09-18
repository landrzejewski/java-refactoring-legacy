import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import {
  LegacyOrderService,
  Order,
  OrderLine,
  type MailGateway,
  type OrderRepository,
} from '../../src/module1/LegacyOrderService.js';

describe('LegacyOrderServiceTest', () => {
  it('placesVipOrderAndInvokesExternalCollaborators', () => {
    let savedTotal: Decimal | undefined;
    let sentMessage: string | undefined;
    const repository: OrderRepository = { save: (_orderId, total) => { savedTotal = total; } };
    const mailGateway: MailGateway = { send: (_recipient, body) => { sentMessage = body; } };
    const service = new LegacyOrderService(repository, mailGateway);
    const order = new Order(
      '9aa026a4-fc39-4af8-a008-d9b831b0ba59',
      'customer@example.com',
      [new OrderLine('BOOK-1', 2, new Decimal('100.00'))],
    );

    const receipt = service.placeOrder(order, 'VIP', false, 'PL');

    expect(receipt.total.toFixed(2)).toBe('236.39');
    expect(receipt.total.decimalPlaces()).toBeLessThanOrEqual(2);
    expect(savedTotal).toBe(receipt.total);
    expect(sentMessage).toBe('Order total: 236.39');
  });
});
