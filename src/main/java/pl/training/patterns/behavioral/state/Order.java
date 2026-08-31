package pl.training.patterns.behavioral.state;

public class Order {

    private OrderState state = OrderStatus.NEW;

    public void pay() {
        state = state.pay();
    }

    public void ship() {
        state = state.ship();
    }

    public void cancel() {
        state = state.cancel();
    }

    public OrderState getState() {
        return state;
    }

}
