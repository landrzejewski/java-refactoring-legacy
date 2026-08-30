package pl.training.module4.encapsulation;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.module4.model.EquipmentType;

public final class AccessorBasedEquipmentCatalog {
    private String name;
    private final Map<EquipmentType, BigDecimal> dailyRates;

    public AccessorBasedEquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = name;
        this.dailyRates = dailyRates;
    }

    public String name() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public Map<EquipmentType, BigDecimal> dailyRates() {
        return dailyRates;
    }
}
