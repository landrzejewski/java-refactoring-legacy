package pl.training.workshop.m5.s31_anticorruptionlayer.step2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorRecord;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: translator modelu (Extract Class z prywatnej metody serwisu). Jedyne miejsce, które
 * zna konwencje dystrybutora: kody statusu, daty "yyyyMMdd", grosze jako tekst, "45%", tytuły
 * wielkimi literami. Zepsuty rekord zgłasza się tutaj, w języku granicy.
 */
public final class DistributorTranslator {
    private static final String ACTIVE = "A";

    public FilmLicense toLicense(DistributorRecord record) {
        return new FilmLicense(
                record.filmCode(),
                title(record.titleUpper()),
                record.statusCode().equals(ACTIVE),
                date(record.validFrom()),
                date(record.validTo()),
                Integer.parseInt(record.share().replace("%", "").strip()),
                new Money(new BigDecimal(record.minGuaranteeCents()).movePointLeft(2)));
    }

    private static String title(String upper) {
        return upper.charAt(0) + upper.substring(1).toLowerCase(Locale.ROOT);
    }

    private static LocalDate date(String legacy) {
        try {
            return LocalDate.parse(legacy, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("invalid legacy date: " + legacy, exception);
        }
    }
}
