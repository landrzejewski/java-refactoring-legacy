package pl.training.workshop.m5.s31_anticorruptionlayer.step1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;
import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorRecord;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: reguły liczą się na FilmLicense, a rekord dystrybutora jest tłumaczony w jednej
 * prywatnej metodzie, zaraz po pobraniu. Serwis wciąż jednak zna klienta i format dystrybutora.
 */
public final class SettlementService {
    private final DistributorFeed feed;

    public SettlementService(DistributorFeed feed) {
        this.feed = Objects.requireNonNull(feed, "feed");
    }

    public boolean canScreen(String filmCode, LocalDate day) {
        DistributorRecord record = feed.fetch(filmCode);
        return record != null && toLicense(record).allowsScreeningOn(day);
    }

    public String settle(String filmCode, LocalDate day, Money revenue) {
        DistributorRecord record = feed.fetch(filmCode);
        if (record == null) {
            return "BRAK LICENCJI " + filmCode;
        }
        FilmLicense license = toLicense(record);
        if (!license.active()) {
            return "LICENCJA ZAWIESZONA " + filmCode;
        }
        if (!license.covers(day)) {
            return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day;
        }
        return "ROZLICZENIE " + license.title() + " " + day + ": przychod " + revenue + ", udzial "
                + license.sharePercent() + "%, minimum " + license.minimumGuarantee()
                + ", dla dystrybutora " + license.dueFor(revenue);
    }

    private static FilmLicense toLicense(DistributorRecord record) {
        String title = record.titleUpper().charAt(0) + record.titleUpper().substring(1).toLowerCase(Locale.ROOT);
        return new FilmLicense(
                record.filmCode(),
                title,
                record.statusCode().equals("A"),
                LocalDate.parse(record.validFrom(), DateTimeFormatter.BASIC_ISO_DATE),
                LocalDate.parse(record.validTo(), DateTimeFormatter.BASIC_ISO_DATE),
                Integer.parseInt(record.share().replace("%", "").strip()),
                new Money(new BigDecimal(record.minGuaranteeCents()).movePointLeft(2)));
    }
}
