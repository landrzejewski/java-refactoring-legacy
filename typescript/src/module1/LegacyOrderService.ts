import { Decimal } from 'decimal.js';
import { IllegalArgumentError } from '../shared/errors.js';

export class LegacyOrderService {
  private readonly repository: OrderRepository;
  private readonly mailGateway: MailGateway;

  constructor(repository: OrderRepository, mailGateway: MailGateway) {
    this.repository = repository;
    this.mailGateway = mailGateway;
  }

  placeOrder(
    order: Order | null,
    customerType: string,
    express: boolean,
    destinationCountry: string,
  ): Receipt {
    if (order == null || order.lines == null || order.lines.length === 0) {
      throw new IllegalArgumentError('Order must contain lines');
    }

    let subtotal = new Decimal(0);

    for (const line of order.lines) {
      let lineValue = line.unitPrice.times(line.quantity);

      if ('VIP' === customerType) {
        lineValue = lineValue.times(new Decimal('0.90'));
      }

      if (line.quantity >= 10) {
        lineValue = lineValue.times(new Decimal('0.95'));
      }

      subtotal = subtotal.plus(lineValue);
    }

    let shipping: Decimal;
    if (express) {
      shipping = new Decimal('39.99');
    } else if (subtotal.comparedTo(new Decimal('200.00')) >= 0) {
      shipping = new Decimal(0);
    } else {
      shipping = new Decimal('14.99');
    }

    let tax: Decimal;
    if ('PL' === destinationCountry) {
      tax = subtotal.times(new Decimal('0.23'));
    } else if ('DE' === destinationCountry) {
      tax = subtotal.times(new Decimal('0.19'));
    } else {
      tax = new Decimal(0);
    }

    const total = subtotal
      .plus(shipping)
      .plus(tax)
      .toDecimalPlaces(2, Decimal.ROUND_HALF_UP);

    this.repository.save(order.id, total);
    this.mailGateway.send(order.customerEmail, 'Order total: ' + total.toFixed(2));

    return new Receipt(order.id, total);
  }
}

export class Order {
  constructor(
    readonly id: string,
    readonly customerEmail: string,
    readonly lines: readonly OrderLine[],
  ) {}
}

export class OrderLine {
  constructor(
    readonly sku: string,
    readonly quantity: number,
    readonly unitPrice: Decimal,
  ) {}
}

export class Receipt {
  constructor(
    readonly orderId: string,
    readonly total: Decimal,
  ) {}

  toString(): string {
    return `Receipt[orderId=${this.orderId}, total=${this.total.toFixed(2)}]`;
  }
}

export interface OrderRepository {
  save(orderId: string, total: Decimal): void;
}

export interface MailGateway {
  send(recipient: string, body: string): void;
}
