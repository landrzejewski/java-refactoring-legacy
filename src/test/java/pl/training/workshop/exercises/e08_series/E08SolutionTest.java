package pl.training.workshop.exercises.e08_series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e08_series.step2.Screening;
import pl.training.workshop.exercises.e08_series.step2.SeriesPlanner;
import pl.training.workshop.shared.Money;

/**
 * Pułapka startu (współdzielona mapa dopłat) i kontrakt rozwiązania:
 * niezależne kopie, wartości domyślne w jednym miejscu, pola wymagane sprawdzane przy budowie.
 */
final class E08SolutionTest {
    private static final LocalDateTime FRIDAY_EVENING = LocalDateTime.of(2026, 11, 6, 18, 0);
    private static final SeriesRequest PREMIERE_SERIES =
            new SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 3, null, null, null, true, null);

    @Test
    void startLeaksThePremiereSurchargeIntoEveryWeekBecauseCopiesShareTheMap() {
        var weeks = new pl.training.workshop.exercises.e08_series.start.SeriesPlanner().plan(PREMIERE_SERIES);
        assertEquals(List.of(true, true, true),
                weeks.stream().map(week -> week.surcharges().containsKey("PREMIERA")).toList(),
                "start: dopłata premierowa trafia do wszystkich tygodni (to błąd, nie kontrakt)");
    }

    @Test
    void solutionKeepsThePremiereSurchargeInTheFirstWeekOnly() {
        var weeks = new SeriesPlanner().plan(PREMIERE_SERIES);
        assertEquals(List.of(true, false, false),
                weeks.stream().map(week -> week.surcharges().containsKey("PREMIERA")).toList(),
                "rozwiązanie: świadoma naprawa, każdy tydzień ma własną mapę dopłat");
    }

    @Test
    void copyIsIndependentFromTheTemplate() {
        Screening template = Screening.builder().title("Diuna").format("IMAX").start(FRIDAY_EVENING).build();
        Screening premiere = template.withSurcharge("PREMIERA", Money.of("5.00"));
        Screening nextWeek = template.withStart(FRIDAY_EVENING.plusWeeks(1));

        assertFalse(template.surcharges().containsKey("PREMIERA"), "szablon nie widzi zmiany w kopii");
        assertTrue(premiere.surcharges().containsKey("PREMIERA"));
        assertEquals(template.surcharges(), nextWeek.surcharges());
        assertThrows(UnsupportedOperationException.class,
                () -> template.surcharges().put("VIP", Money.ZERO), "mapy dopłat nie da się zmienić z zewnątrz");
    }

    @Test
    void defaultsLiveInTheBuilderOnly() {
        Screening screening = Screening.builder().title("Kraina Lodu").format("3D").start(FRIDAY_EVENING).build();
        assertEquals("A", screening.hall());
        assertEquals(Money.of("32.00"), screening.basePrice());
        assertEquals(Map.of("VIP", Money.of("10.00"), "OKULARY", Money.of("3.00")), screening.surcharges());
        assertTrue(screening.subtitles());
        assertFalse(screening.dubbing());
        assertEquals(12, screening.ageLimit());
    }

    @Test
    void builderRejectsMissingRequiredFieldsWithAClearMessage() {
        assertEquals("title must not be blank", assertThrows(IllegalArgumentException.class,
                () -> Screening.builder().format("2D").start(FRIDAY_EVENING).build()).getMessage());
        assertEquals("unknown format: null", assertThrows(IllegalArgumentException.class,
                () -> Screening.builder().title("Amator").start(FRIDAY_EVENING).build()).getMessage());
        assertEquals("start is required", assertThrows(IllegalArgumentException.class,
                () -> Screening.builder().title("Amator").format("2D").build()).getMessage());
    }

    @Test
    void screeningCannotBeBuiltAroundTheBuilderWithNulls() {
        assertEquals(0, Screening.class.getConstructors().length, "jedyna droga to Screening.builder()");
    }
}
