package pl.training.module3.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record DeliveryQuote(
        String customerEmail,
        ShippingMethod method,
        Parcel parcel,
        BigDecimal price) {
    public DeliveryQuote {
        Objects.requireNonNull(customerEmail, "customerEmail");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(parcel, "parcel");
        Objects.requireNonNull(price, "price");

        if (customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer email must not be blank");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }

        price = price.setScale(2, RoundingMode.UNNECESSARY);
    }
}
