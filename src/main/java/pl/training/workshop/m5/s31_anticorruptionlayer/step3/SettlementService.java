package pl.training.workshop.m5.s31_anticorruptionlayer.step3;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: serwis domeny zależy tylko od portu FilmLicenses i mówi wyłącznie językiem kina.
 * Żaden typ dystrybutora nie przekracza warstwy antykorupcyjnej (DistributorAcl).
 */
public final class SettlementService {
    private final FilmLicenses licenses;

    public SettlementService(FilmLicenses licenses) {
        this.licenses = Objects.requireNonNull(licenses, "licenses");
    }

    public boolean canScreen(String filmCode, LocalDate day) {
        return licenses.find(filmCode).map(license -> license.allowsScreeningOn(day)).orElse(false);
    }

    public String settle(String filmCode, LocalDate day, Money revenue) {
        Optional<FilmLicense> found = licenses.find(filmCode);
        if (found.isEmpty()) {
            return "BRAK LICENCJI " + filmCode;
        }
        FilmLicense license = found.get();
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
