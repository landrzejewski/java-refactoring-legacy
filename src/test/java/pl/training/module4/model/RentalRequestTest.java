package pl.training.module4.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class RentalRequestTest {
    @Test
    void rejectsInvalidInput() {
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new RentalRequest(
                                " ",
                                EquipmentType.DRILL,
                                1,
                                false,
                                false)),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                0,
                                false,
                                false)));
    }
}
