import type { OrderState } from './OrderState.js';
import { OrderStatus } from './OrderStatus.js';

export class Order {
  private state: OrderState = OrderStatus.NEW;

  pay(): void {
    this.state = this.state.pay();
  }

  ship(): void {
    this.state = this.state.ship();
  }

  cancel(): void {
    this.state = this.state.cancel();
  }

  getState(): OrderState {
    return this.state;
  }
}
