package pl.training.module4.encapsulation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import pl.training.module4.model.EquipmentType;

public final class EquipmentCatalog {
    private String name;
    private final EnumMap<EquipmentType, BigDecimal> dailyRates;

    public EquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = validName(name);
        Objects.requireNonNull(dailyRates, "dailyRates");

        this.dailyRates = new EnumMap<>(EquipmentType.class);
        dailyRates.forEach(this::changeDailyRate);
    }

    public String name() {
        return name;
    }

    public void renameTo(String newName) {
        name = validName(newName);
    }

    public BigDecimal dailyRateFor(EquipmentType type) {
        Objects.requireNonNull(type, "type");
        BigDecimal rate = dailyRates.get(type);
        if (rate == null) {
            throw new IllegalArgumentException("Missing daily rate for " + type);
        }
        return rate;
    }

    public void changeDailyRate(EquipmentType type, BigDecimal newRate) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(newRate, "newRate");
        BigDecimal normalizedRate =
                newRate.setScale(2, RoundingMode.HALF_UP);
        if (normalizedRate.signum() <= 0) {
            throw new IllegalArgumentException("Daily rate must be positive");
        }
        dailyRates.put(type, normalizedRate);
    }

    public Map<EquipmentType, BigDecimal> dailyRates() {
        return Map.copyOf(dailyRates);
    }

    private static String validName(String value) {
        Objects.requireNonNull(value, "name");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Catalog name must not be blank");
        }
        return value;
    }
}
