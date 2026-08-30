package pl.training.module4.encapsulation;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.module4.model.EquipmentType;

public final class LegacyEquipmentCatalog {
    public String name;
    public final Map<EquipmentType, BigDecimal> dailyRates;

    public LegacyEquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = name;
        this.dailyRates = dailyRates;
    }
}
