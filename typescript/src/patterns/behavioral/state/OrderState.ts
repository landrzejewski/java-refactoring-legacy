import { IllegalStateError } from '../../../shared/errors.js';

export interface OrderState {
  pay(): OrderState;
  ship(): OrderState;
  cancel(): OrderState;
}

/** Java: the default methods of interface OrderState - every transition is rejected unless overridden. */
export abstract class DefaultOrderState implements OrderState {
  pay(): OrderState {
    throw new IllegalStateError(`Payment is not allowed in state ${this.toString()}`);
  }

  ship(): OrderState {
    throw new IllegalStateError(`Shipping is not allowed in state ${this.toString()}`);
  }

  cancel(): OrderState {
    throw new IllegalStateError(`Cancellation is not allowed in state ${this.toString()}`);
  }
}
