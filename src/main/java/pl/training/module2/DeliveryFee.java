package pl.training.module2;

public final class DeliveryFee {
    private DeliveryFee() {
    }

    public static int fee(boolean premium) {
        int fee = 100;

        if (premium) {
            fee = 0;
        }

        return fee;
    }
}
