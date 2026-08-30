package pl.training.module4.model;

import java.util.Objects;

public record RentalRequest(
        String customerName,
        EquipmentType equipmentType,
        int days,
        boolean insurance,
        boolean delivery) {
    public RentalRequest {
        Objects.requireNonNull(customerName, "customerName");
        Objects.requireNonNull(equipmentType, "equipmentType");

        if (customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be positive");
        }
    }
}
