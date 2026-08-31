package pl.training.patterns.behavioral.state;

public enum OrderStatus implements OrderState {

    NEW {
        @Override
        public OrderState pay() {
            return PAID;
        }

        @Override
        public OrderState cancel() {
            return CANCELLED;
        }
    },
    PAID {
        @Override
        public OrderState ship() {
            return SHIPPED;
        }

        @Override
        public OrderState cancel() {
            return CANCELLED;
        }
    },
    SHIPPED,
    CANCELLED

}
