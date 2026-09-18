import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../shared/errors.js';
import { requireNonNull } from '../shared/requireNonNull.js';

export class OrderPlacementService {
  private readonly catalog: ProductCatalog;
  private readonly paymentGateway: PaymentGateway;
  private readonly repository: OrderRepository;
  private readonly eventPublisher: EventPublisher;

  constructor(
    catalog: ProductCatalog,
    paymentGateway: PaymentGateway,
    repository: OrderRepository,
    eventPublisher: EventPublisher,
  ) {
    this.catalog = requireNonNull(catalog);
    this.paymentGateway = requireNonNull(paymentGateway);
    this.repository = requireNonNull(repository);
    this.eventPublisher = requireNonNull(eventPublisher);
  }

  place(sku: string, quantity: number, paymentToken: string): PlacedOrder {
    if (quantity <= 0) {
      throw new IllegalArgumentError('Quantity must be positive');
    }

    const total = this.catalog.priceFor(sku)
      .times(quantity)
      .toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const authorizationId = this.paymentGateway.charge(paymentToken, total);
    const orderId = this.repository.save(
      new OrderDraft(sku, quantity, total, authorizationId));

    this.eventPublisher.publish(new OrderPlaced(orderId, total));
    return new PlacedOrder(orderId, total, authorizationId);
  }
}

// W Javie interfejsy i rekordy są zagnieżdżone w OrderPlacementService;
// w TS eksportujemy je z tego samego modułu.
export interface ProductCatalog {
  priceFor(sku: string): Decimal;
}

export interface PaymentGateway {
  charge(paymentToken: string, amount: Decimal): string;
}

export interface OrderRepository {
  save(order: OrderDraft): number;
}

export interface EventPublisher {
  publish(event: OrderPlaced): void;
}

export class OrderDraft {
  constructor(
    readonly sku: string,
    readonly quantity: number,
    readonly total: Decimal,
    readonly authorizationId: string,
  ) {}
}

export class OrderPlaced {
  constructor(
    readonly orderId: number,
    readonly total: Decimal,
  ) {}

  toString(): string {
    return `OrderPlaced[orderId=${this.orderId}, total=${this.total.toFixed(2)}]`;
  }
}

export class PlacedOrder {
  constructor(
    readonly orderId: number,
    readonly total: Decimal,
    readonly authorizationId: string,
  ) {}

  toString(): string {
    return `PlacedOrder[orderId=${this.orderId}, total=${this.total.toFixed(2)}, `
      + `authorizationId=${this.authorizationId}]`;
  }
}
