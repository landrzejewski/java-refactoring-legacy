package pl.training.workshop.m5.s31_anticorruptionlayer.step2;

import java.time.LocalDate;
import java.util.Objects;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;
import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorRecord;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: serwis nie zna już konwencji dystrybutora - tłumaczy je DistributorTranslator.
 * Nadal jednak pobiera DistributorRecord i obsługuje jego null; schowamy to za portem w kroku 3.
 */
public final class SettlementService {
    private final DistributorFeed feed;
    private final DistributorTranslator translator = new DistributorTranslator();

    public SettlementService(DistributorFeed feed) {
        this.feed = Objects.requireNonNull(feed, "feed");
    }

    public boolean canScreen(String filmCode, LocalDate day) {
        DistributorRecord record = feed.fetch(filmCode);
        return record != null && translator.toLicense(record).allowsScreeningOn(day);
    }

    public String settle(String filmCode, LocalDate day, Money revenue) {
        DistributorRecord record = feed.fetch(filmCode);
        if (record == null) {
            return "BRAK LICENCJI " + filmCode;
        }
        FilmLicense license = translator.toLicense(record);
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
}
