package pl.training.workshop.m4.s01_rename;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności: ten sam CSV z wywołania w Javie i z zadania uruchamianego z konfiguracji.
 * Drugi test to ten, który "łapie" Rename: IDE przemianowuje Javę, ale nie tekst konfiguracji.
 */
final class S01EquivalenceTest {
    static final List<Sale> SALES = List.of(
            new Sale("Diuna", 2, new BigDecimal("80.00"), true),
            new Sale("Amator", 1, new BigDecimal("25.00"), false),
            new Sale("Diuna", 1, new BigDecimal("40.00"), false),
            new Sale("Kraina Lodu", 3, new BigDecimal("96.00"), true));

    @TestFactory
    @SuppressWarnings("deprecation")
    Stream<DynamicTest> everyStepPrintsTheSameCsv() {
        return Scene.<List<Sale>, String>variants()
                .variant("start", s -> new pl.training.workshop.m4.s01_rename.start.SalesReport().calc2(s, false))
                .variant("step1", s -> new pl.training.workshop.m4.s01_rename.step1.SalesReport().calc2(s, false))
                .variant("step2", s -> new pl.training.workshop.m4.s01_rename.step2.SalesReport()
                        .revenueCsv(s, false))
                .variant("step3", s -> new pl.training.workshop.m4.s01_rename.step3.SalesReport()
                        .revenueCsv(s, false))
                .expect("wszystkie sprzedaże, nagłówek t;n;d", SALES, """
                        t;n;d
                        Amator;1;25.00
                        Diuna;3;120.00
                        Kraina Lodu;3;96.00
                        """)
                .expect("brak sprzedaży - sam nagłówek", List.of(), "t;n;d\n")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> everyStepRunsTheConfiguredJob() {
        return Scene.<List<Sale>, String>variants()
                .variant("start", new pl.training.workshop.m4.s01_rename.start.ReportJob()::run)
                .variant("step1", new pl.training.workshop.m4.s01_rename.step1.ReportJob()::run)
                .variant("step2", new pl.training.workshop.m4.s01_rename.step2.ReportJob()::run)
                .variant("step3", new pl.training.workshop.m4.s01_rename.step3.ReportJob()::run)
                .expect("konfiguracja: report.method=calc2, tylko online", SALES, """
                        t;n;d
                        Diuna;2;80.00
                        Kraina Lodu;3;96.00
                        """)
                .tests();
    }
}
