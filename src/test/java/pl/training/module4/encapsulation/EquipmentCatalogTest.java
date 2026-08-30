package pl.training.module4.encapsulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.module4.model.EquipmentType;

final class EquipmentCatalogTest {
    @Test
    void legacyCatalogSharesItsMutableMapWithTheCaller() {
        EnumMap<EquipmentType, BigDecimal> source = rates();
        LegacyEquipmentCatalog catalog = new LegacyEquipmentCatalog(
                "Summer rental",
                source);

        source.put(EquipmentType.DRILL, new BigDecimal("1.00"));

        assertEquals(
                new BigDecimal("1.00"),
                catalog.dailyRates.get(EquipmentType.DRILL));
    }

    @Test
    void accessorBasedCatalogPreservesAliasesDuringControlledMigration() {
        EnumMap<EquipmentType, BigDecimal> source = rates();
        AccessorBasedEquipmentCatalog catalog =
                new AccessorBasedEquipmentCatalog("Summer rental", source);

        source.put(EquipmentType.DRILL, new BigDecimal("1.00"));
        catalog.setName(" ");

        assertSame(source, catalog.dailyRates());
        assertEquals(
                new BigDecimal("1.00"),
                catalog.dailyRates().get(EquipmentType.DRILL));
        assertEquals(" ", catalog.name());
    }

    @Test
    void encapsulatedCatalogOwnsRatesAndReturnsUnmodifiableSnapshots() {
        EnumMap<EquipmentType, BigDecimal> source = rates();
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                source);
        Map<EquipmentType, BigDecimal> snapshot = catalog.dailyRates();

        source.put(EquipmentType.DRILL, new BigDecimal("1.00"));
        assertEquals(
                new BigDecimal("39.99"),
                catalog.dailyRateFor(EquipmentType.DRILL));

        catalog.changeDailyRate(
                EquipmentType.DRILL,
                new BigDecimal("42.00"));

        assertEquals(
                new BigDecimal("39.99"),
                snapshot.get(EquipmentType.DRILL));
        assertEquals(
                new BigDecimal("42.00"),
                catalog.dailyRateFor(EquipmentType.DRILL));
        assertThrows(
                UnsupportedOperationException.class,
                () -> snapshot.put(
                        EquipmentType.GENERATOR,
                        new BigDecimal("120.00")));
    }

    @Test
    void changesNameOnlyThroughValidatedOperation() {
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                rates());

        catalog.renameTo("Winter rental");

        assertEquals("Winter rental", catalog.name());
        assertThrows(
                IllegalArgumentException.class,
                () -> catalog.renameTo(" "));
    }

    @Test
    void normalizesRateBeforeCheckingItsInvariant() {
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                rates());

        catalog.changeDailyRate(
                EquipmentType.DRILL,
                new BigDecimal("42.005"));

        assertEquals(
                new BigDecimal("42.01"),
                catalog.dailyRateFor(EquipmentType.DRILL));
        assertThrows(
                IllegalArgumentException.class,
                () -> catalog.changeDailyRate(
                        EquipmentType.DRILL,
                        new BigDecimal("0.004")));
    }

    private static EnumMap<EquipmentType, BigDecimal> rates() {
        EnumMap<EquipmentType, BigDecimal> rates =
                new EnumMap<>(EquipmentType.class);
        rates.put(EquipmentType.DRILL, new BigDecimal("39.99"));
        return rates;
    }
}
