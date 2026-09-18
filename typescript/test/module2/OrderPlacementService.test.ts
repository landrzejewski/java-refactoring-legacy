import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import {
  OrderDraft,
  OrderPlaced,
  OrderPlacementService,
  type EventPublisher,
  type OrderRepository,
  type PaymentGateway,
  type ProductCatalog,
} from '../../src/module2/OrderPlacementService.js';

class InMemoryOrderRepository implements OrderRepository {
  private readonly orders = new Map<number, OrderDraft>();
  private nextId = 1;

  save(order: OrderDraft): number {
    const id = this.nextId++;
    this.orders.set(id, order);
    return id;
  }

  find(orderId: number): OrderDraft | undefined {
    return this.orders.get(orderId);
  }
}

class RecordingEventPublisher implements EventPublisher {
  private readonly events: OrderPlaced[] = [];

  publish(event: OrderPlaced): void {
    this.events.push(event);
  }

  publishedEvents(): readonly OrderPlaced[] {
    return [...this.events];
  }
}

class ExpectingPaymentGateway implements PaymentGateway {
  private calls = 0;

  constructor(
    private readonly expectedToken: string,
    private readonly expectedAmount: Decimal,
    private readonly authorizationId: string,
  ) {}

  charge(paymentToken: string, amount: Decimal): string {
    expect(paymentToken).toBe(this.expectedToken);
    expect(amount).toEqual(this.expectedAmount);
    this.calls++;
    return this.authorizationId;
  }

  verify(): void {
    expect(this.calls).toBe(1);
  }
}

describe('OrderPlacementServiceTest', () => {
  it('placesOrderUsingStubFakeAndSpy', () => {
    const catalogStub: ProductCatalog = { priceFor: () => new Decimal('12.50') };
    const paymentStub: PaymentGateway = { charge: () => 'AUTH-7' };
    const repositoryFake = new InMemoryOrderRepository();
    const publisherSpy = new RecordingEventPublisher();
    const service = new OrderPlacementService(
      catalogStub,
      paymentStub,
      repositoryFake,
      publisherSpy);

    const result = service.place('BOOK', 2, 'TOKEN-1');

    expect(result.total).toEqual(new Decimal('25.00'));
    expect(result.authorizationId).toBe('AUTH-7');
    expect(repositoryFake.find(result.orderId))
      .toEqual(new OrderDraft('BOOK', 2, new Decimal('25.00'), 'AUTH-7'));
    expect(publisherSpy.publishedEvents())
      .toEqual([new OrderPlaced(result.orderId, new Decimal('25.00'))]);
  });

  it('verifiesPaymentProtocolUsingMock', () => {
    const catalogStub: ProductCatalog = { priceFor: () => new Decimal('40.00') };
    const paymentMock = new ExpectingPaymentGateway('TOKEN-2', new Decimal('120.00'), 'AUTH-9');
    const repositoryFake = new InMemoryOrderRepository();
    const publisherStub: EventPublisher = { publish: () => {} };
    const service = new OrderPlacementService(
      catalogStub,
      paymentMock,
      repositoryFake,
      publisherStub);

    service.place('COURSE', 3, 'TOKEN-2');

    paymentMock.verify();
  });
});
