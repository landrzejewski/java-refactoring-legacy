package pl.training.workshop.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Golden master całego starego systemu. Każda zmiana w legacy, która
 * zmienia choćby jedną wiadomość, kwotę czy kolejność, zostanie wykryta.
 * Zatwierdzony wynik: src/test/resources/workshop/cinema-manager.approved.txt
 */
final class CinemaManagerGoldenMasterTest {
    @Test
    void oneDayOfCinemaProducesApprovedOutput() throws IOException {
        assertEquals(approved(), CinemaManagerScript.run());
    }

    private static String approved() throws IOException {
        try (InputStream in = CinemaManagerGoldenMasterTest.class
                .getResourceAsStream("/workshop/cinema-manager.approved.txt")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
