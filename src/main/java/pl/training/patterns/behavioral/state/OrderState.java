package pl.training.patterns.behavioral.state;

public interface OrderState {

    default OrderState pay() {
        throw new IllegalStateException("Payment is not allowed in state " + this);
    }

    default OrderState ship() {
        throw new IllegalStateException("Shipping is not allowed in state " + this);
    }

    default OrderState cancel() {
        throw new IllegalStateException("Cancellation is not allowed in state " + this);
    }

}
