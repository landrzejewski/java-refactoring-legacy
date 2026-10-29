package pl.training.workshop.exercises.e06_movieinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Infrastruktura (nie zmieniamy): plik z mapowaniem obcych kodów wieku na kategorie kina,
 * czytany z classpath. Odczyt jest kosztowny (dysk, parsowanie), więc go liczymy - tak jak
 * w produkcji liczy się czas i operacje wejścia-wyjścia.
 */
public final class AgeRatingResource {
    private static final String PATH = "/exercises/e06_movieinfo/age-ratings.csv";

    private static int reads;

    private AgeRatingResource() {
    }

    public static List<String> readLines() {
        reads++;
        try (InputStream in = AgeRatingResource.class.getResourceAsStream(PATH)) {
            if (in == null) {
                throw new IllegalStateException("missing resource " + PATH);
            }
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().toList();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /** Ile razy plik został wczytany od startu JVM. */
    public static int reads() {
        return reads;
    }
}
