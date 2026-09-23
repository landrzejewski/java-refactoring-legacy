package pl.training.workshop.m6.s03_typecode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s03_typecode.step3.Format;
import pl.training.workshop.m6.s03_typecode.step3.FormatCodes;

/** Granica trwałości: w bazie zostaje stabilny kod, nie ordinal ani nazwa stałej. */
final class S03SolutionTest {
    @Test
    void persistentCodeIsNotTheOrdinal() {
        assertNotEquals(Format.IMAX.ordinal(), FormatCodes.toCode(Format.IMAX));
        assertEquals(3, FormatCodes.toCode(Format.IMAX));
    }

    @Test
    void everyFormatSurvivesRoundTripThroughTheMapper() {
        for (Format format : Format.values()) {
            assertEquals(format, FormatCodes.fromCode(FormatCodes.toCode(format)));
        }
    }
}
