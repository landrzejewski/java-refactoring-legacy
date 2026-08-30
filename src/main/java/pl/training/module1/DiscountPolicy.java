package pl.training.module1;

public final class DiscountPolicy {
    private DiscountPolicy() {
    }

    public static int discountPercent(int orderValue, boolean vip) {
        int discount = 0;

        if (orderValue >= 100) {
            discount += 10;
        }

        if (vip) {
            discount += 5;
        }

        return discount;
    }
}
