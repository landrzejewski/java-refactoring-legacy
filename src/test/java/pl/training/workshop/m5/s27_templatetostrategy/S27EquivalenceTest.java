package pl.training.workshop.m5.s27_templatetostrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Wszystkie cztery kombinacje okres x format na tych samych danych, granice tygodnia
 * (niedziela przed, poniedziałek po), dzień bez sprzedaży i kolejność błędów wyboru.
 */
final class S27EquivalenceTest {
    record Request(String period, String format, LocalDate day) {
    }

    private static final List<ScreeningSale> SALES = List.of(
            sale("2026-10-18", "Amator", 4, "100.00"),
            sale("2026-10-19", "Diuna", 10, "400.00"),
            sale("2026-10-20", "Diuna", 5, "200.00"),
            sale("2026-10-20", "Kraina Lodu", 3, "81.00"),
            sale("2026-10-20", "Szybcy; reedycja", 1, "25.00"),
            sale("2026-10-25", "Amator", 2, "50.00"),
            sale("2026-10-26", "Diuna", 7, "280.00"));

    @TestFactory
    Stream<DynamicTest> everyStepRendersEveryCombinationTheSame() {
        return Scene.<Request, String>variants()
                .variant("start", render((p, f) -> pl.training.workshop.m5.s27_templatetostrategy.start.Reports.of(p, f)::render))
                .variant("step1", render((p, f) -> pl.training.workshop.m5.s27_templatetostrategy.step1.Reports.of(p, f)::render))
                .variant("step2", render((p, f) -> pl.training.workshop.m5.s27_templatetostrategy.step2.Reports.of(p, f)::render))
                .variant("step3", render((p, f) -> pl.training.workshop.m5.s27_templatetostrategy.step3.Reports.of(p, f)::render))
                .expect("dzień CSV (escapowanie średnika)", request("DAILY", "CSV", "2026-10-20"), """
                        # raport dzien 2026-10-20
                        film;bilety;przychod
                        Diuna;5;200.00
                        Kraina Lodu;3;81.00
                        "Szybcy; reedycja";1;25.00
                        SUMA;9;306.00
                        """)
                .expect("dzień tekst", request("DAILY", "TEXT", "2026-10-20"), """
                        RAPORT DZIEN 2026-10-20
                        -------------------------------------
                        Diuna                    5     200.00
                        Kraina Lodu              3      81.00
                        Szybcy; reedycja         1      25.00
                        -------------------------------------
                        RAZEM                    9     306.00
                        """)
                .expect("tydzień CSV ze środy (sumy po tytule)", request("WEEKLY", "CSV", "2026-10-22"), """
                        # raport tydzien 2026-10-19..2026-10-25
                        film;bilety;przychod
                        Amator;2;50.00
                        Diuna;15;600.00
                        Kraina Lodu;3;81.00
                        "Szybcy; reedycja";1;25.00
                        SUMA;21;756.00
                        """)
                .expect("tydzień tekst z niedzieli (ten sam tydzień)", request("WEEKLY", "TEXT", "2026-10-25"), """
                        RAPORT TYDZIEN 2026-10-19..2026-10-25
                        -------------------------------------
                        Amator                   2      50.00
                        Diuna                   15     600.00
                        Kraina Lodu              3      81.00
                        Szybcy; reedycja         1      25.00
                        -------------------------------------
                        RAZEM                   21     756.00
                        """)
                .expect("tydzień od poniedziałku: następny tydzień", request("WEEKLY", "CSV", "2026-10-26"), """
                        # raport tydzien 2026-10-26..2026-11-01
                        film;bilety;przychod
                        Diuna;7;280.00
                        SUMA;7;280.00
                        """)
                .expect("dzień bez sprzedaży", request("DAILY", "CSV", "2026-10-21"), """
                        # raport dzien 2026-10-21
                        film;bilety;przychod
                        SUMA;0;0.00
                        """)
                .expect("nieznany okres", request("MONTHLY", "CSV", "2026-10-20"), "ERROR unknown period: MONTHLY")
                .expect("nieznany format", request("DAILY", "PDF", "2026-10-20"), "ERROR unknown format: PDF")
                .expect("najpierw okres, potem format", request("MONTHLY", "PDF", "2026-10-20"),
                        "ERROR unknown period: MONTHLY")
                .tests();
    }

    interface ReportFactory {
        Renderer of(String period, String format);
    }

    interface Renderer {
        String render(LocalDate day, List<ScreeningSale> sales);
    }

    private static Function<Request, String> render(ReportFactory reports) {
        return request -> {
            try {
                return reports.of(request.period(), request.format()).render(request.day(), SALES);
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }

    private static Request request(String period, String format, String day) {
        return new Request(period, format, LocalDate.parse(day));
    }

    private static ScreeningSale sale(String day, String title, int tickets, String revenue) {
        return new ScreeningSale(LocalDate.parse(day), title, tickets, Money.of(revenue));
    }
}
