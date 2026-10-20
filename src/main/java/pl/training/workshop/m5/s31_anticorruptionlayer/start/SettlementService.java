package pl.training.workshop.m5.s31_anticorruptionlayer.start;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;
import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorRecord;
import pl.training.workshop.shared.Money;

/**
 * Start: rozliczenie z dystrybutorem myśli w modelu dystrybutora. Logika domeny (czy wolno
 * grać film, ile się należy) jest przemieszana z dekodowaniem cudzych konwencji: kody statusu,
 * daty "yyyyMMdd", grosze jako tekst, "45%", tytuły wielkimi literami. Każda zmiana formatu
 * u dystrybutora rozlewa się po naszych regułach.
 */
public final class SettlementService {
    private final DistributorFeed feed;

    public SettlementService(DistributorFeed feed) {
        this.feed = Objects.requireNonNull(feed, "feed");
    }

    public boolean canScreen(String filmCode, LocalDate day) {
        DistributorRecord record = feed.fetch(filmCode);
        return record != null
                && record.statusCode().equals("A")
                && !day.isBefore(LocalDate.parse(record.validFrom(), DateTimeFormatter.BASIC_ISO_DATE))
                && !day.isAfter(LocalDate.parse(record.validTo(), DateTimeFormatter.BASIC_ISO_DATE));
    }

    public String settle(String filmCode, LocalDate day, Money revenue) {
        DistributorRecord record = feed.fetch(filmCode);
        if (record == null) {
            return "BRAK LICENCJI " + filmCode;
        }
        if (!record.statusCode().equals("A")) {
            return "LICENCJA ZAWIESZONA " + filmCode;
        }
        if (!canScreen(filmCode, day)) {
            return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day;
        }
        int percent = Integer.parseInt(record.share().replace("%", "").strip());
        Money minimum = new Money(new BigDecimal(record.minGuaranteeCents()).movePointLeft(2));
        Money due = revenue.percent(percent).max(minimum);
        String title = record.titleUpper().charAt(0) + record.titleUpper().substring(1).toLowerCase(Locale.ROOT);
        return "ROZLICZENIE " + title + " " + day + ": przychod " + revenue + ", udzial " + percent
                + "%, minimum " + minimum + ", dla dystrybutora " + due;
    }
}
