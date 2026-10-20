import { DefaultOrderState, type OrderState } from './OrderState.js';

/** Java: enum OrderStatus implements OrderState with constant-specific bodies -> class with static instances. */
export class OrderStatus extends DefaultOrderState {
  static readonly NEW: OrderStatus = new (class extends OrderStatus {
    override pay(): OrderState {
      return OrderStatus.PAID;
    }

    override cancel(): OrderState {
      return OrderStatus.CANCELLED;
    }
  })('NEW');

  static readonly PAID: OrderStatus = new (class extends OrderStatus {
    override ship(): OrderState {
      return OrderStatus.SHIPPED;
    }

    override cancel(): OrderState {
      return OrderStatus.CANCELLED;
    }
  })('PAID');

  static readonly SHIPPED: OrderStatus = new OrderStatus('SHIPPED');

  static readonly CANCELLED: OrderStatus = new OrderStatus('CANCELLED');

  protected constructor(readonly name: string) {
    super();
  }

  static values(): readonly OrderStatus[] {
    return [OrderStatus.NEW, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.CANCELLED];
  }

  override toString(): string {
    return this.name;
  }
}
