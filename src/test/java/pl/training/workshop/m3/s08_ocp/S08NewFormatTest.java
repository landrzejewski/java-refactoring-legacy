package pl.training.workshop.m3.s08_ocp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Nowy format 4DX to zmiana zachowania, więc bez start (edytowanego na żywo):
 * w kroku 2 nieznany, w kroku 3 obsłużony bez zmiany ScreeningOffer.
 */
final class S08NewFormatTest {
    @Test
    void step2DoesNotKnow4dxYet() {
        assertThrows(IllegalArgumentException.class,
                () -> new pl.training.workshop.m3.s08_ocp.step2.ScreeningOffer().price("4DX", false));
    }

    @Test
    void step3Prices4dxWithGlasses() {
        var offer = new pl.training.workshop.m3.s08_ocp.step3.ScreeningOffer();
        assertEquals(new BigDecimal("48.00"), offer.price("4DX", false));
        assertEquals(new BigDecimal("45.00"), offer.price("4DX", true));
        assertEquals("4DX - ruchome fotele", offer.label("4DX"));
    }
}
