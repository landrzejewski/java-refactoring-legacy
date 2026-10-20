package pl.training.workshop.m5.s31_anticorruptionlayer;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Rozliczenia i dopuszczenie do emisji na trzech rekordach dystrybutora: udział vs minimalna
 * gwarancja, zaokrąglenie, granice okresu licencji (włącznie), "45%" i " 40 %", licencja
 * zawieszona i nieznany film.
 */
final class S31EquivalenceTest {
    private static final DistributorFeed FEED = new DistributorFeed(List.of(
            new DistributorRecord("DIU", "DIUNA", "A", "20261001", "20261031", "50000", "45%"),
            new DistributorRecord("KRL", "KRAINA LODU", "A", "20261015", "20261115", "30000", " 40 %"),
            new DistributorRecord("AMA", "AMATOR", "S", "20260101", "20261231", "10000", "50%")));

    @TestFactory
    Stream<DynamicTest> everyStepSettlesTheSame() {
        return Scene.<String, String>variants()
                .variant("start", line -> {
                    var service = pl.training.workshop.m5.s31_anticorruptionlayer.start.SettlementModule
                            .settlementService(FEED);
                    return run(line, service::settle, service::canScreen);
                })
                .variant("step1", line -> {
                    var service = pl.training.workshop.m5.s31_anticorruptionlayer.step1.SettlementModule
                            .settlementService(FEED);
                    return run(line, service::settle, service::canScreen);
                })
                .variant("step2", line -> {
                    var service = pl.training.workshop.m5.s31_anticorruptionlayer.step2.SettlementModule
                            .settlementService(FEED);
                    return run(line, service::settle, service::canScreen);
                })
                .variant("step3", line -> {
                    var service = pl.training.workshop.m5.s31_anticorruptionlayer.step3.SettlementModule
                            .settlementService(FEED);
                    return run(line, service::settle, service::canScreen);
                })
                .expect("udział powyżej minimum", "settle DIU 2026-10-20 2000.00",
                        "ROZLICZENIE Diuna 2026-10-20: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00")
                .expect("minimalna gwarancja", "settle DIU 2026-10-20 600.00",
                        "ROZLICZENIE Diuna 2026-10-20: przychod 600.00, udzial 45%, minimum 500.00, dla dystrybutora 500.00")
                .expect("udział ze spacjami i zaokrąglenie", "settle KRL 2026-10-20 1234.57",
                        "ROZLICZENIE Kraina lodu 2026-10-20: przychod 1234.57, udzial 40%, minimum 300.00, "
                                + "dla dystrybutora 493.83")
                .expect("pierwszy dzień licencji", "settle DIU 2026-10-01 2000.00",
                        "ROZLICZENIE Diuna 2026-10-01: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00")
                .expect("ostatni dzień licencji", "settle DIU 2026-10-31 2000.00",
                        "ROZLICZENIE Diuna 2026-10-31: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00")
                .expect("dzień po licencji", "settle DIU 2026-11-01 2000.00", "LICENCJA NIEWAZNA DIU w dniu 2026-11-01")
                .expect("dzień przed licencją", "settle DIU 2026-09-30 2000.00", "LICENCJA NIEWAZNA DIU w dniu 2026-09-30")
                .expect("licencja zawieszona", "settle AMA 2026-10-20 100.00", "LICENCJA ZAWIESZONA AMA")
                .expect("nieznany film", "settle XYZ 2026-10-20 100.00", "BRAK LICENCJI XYZ")
                .expect("emisja w okresie", "can DIU 2026-10-20", "true")
                .expect("emisja po okresie", "can DIU 2026-11-01", "false")
                .expect("emisja przed okresem", "can KRL 2026-10-14", "false")
                .expect("emisja w ostatnim dniu", "can KRL 2026-11-15", "true")
                .expect("emisja zawieszonej", "can AMA 2026-10-20", "false")
                .expect("emisja nieznanego", "can XYZ 2026-10-20", "false")
                .tests();
    }

    interface Settle {
        String settle(String filmCode, LocalDate day, Money revenue);
    }

    /** Wiersz: "settle KOD DZIEN PRZYCHOD" albo "can KOD DZIEN". */
    private static String run(String line, Settle settle, BiPredicate<String, LocalDate> canScreen) {
        String[] parts = line.split(" ");
        LocalDate day = LocalDate.parse(parts[2]);
        return parts[0].equals("can")
                ? String.valueOf(canScreen.test(parts[1], day))
                : settle.settle(parts[1], day, Money.of(parts[3]));
    }
}
