package pl.training.workshop.m5.s08_composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Pułapka self-use przy dziedziczeniu i pułapki delegowania - dokumentacja zachowania każdego wariantu. */
final class S08SolutionTest {
    private static final List<String> ROW_H = List.of("H7", "H8");

    @Test
    void startCountsBulkSelectionTwiceBecauseAddAllCallsAdd() {
        var selection = new pl.training.workshop.m5.s08_composition.start.SeatSelection();
        selection.addAll(ROW_H);
        assertEquals(2, selection.size());
        assertEquals(4, selection.clicks(), "pułapka: addAll() z LinkedHashSet woła nasze add()");
    }

    @Test
    void startExposesWholeSetApiThatBypassesTheCounter() {
        var selection = new pl.training.workshop.m5.s08_composition.start.SeatSelection();
        selection.add("H7");
        Set<?> inheritedApi = assertInstanceOf(Set.class, (Object) selection);
        inheritedApi.clear();
        assertEquals(0, selection.size());
        assertEquals(1, selection.clicks(), "clear() z odziedziczonego API omija licznik");
    }

    @Test
    void delegationCountsBulkSelectionOnce() {
        var step1 = new pl.training.workshop.m5.s08_composition.step1.SeatSelection();
        step1.addAll(ROW_H);
        assertEquals(2, step1.clicks());
        var step2 = new pl.training.workshop.m5.s08_composition.step2.SeatSelection();
        step2.addAll(ROW_H);
        assertEquals(2, step2.clicks());
    }

    @Test
    void generatedGetterLeaksTheDelegate() {
        var selection = new pl.training.workshop.m5.s08_composition.step1.SeatSelection();
        selection.getSeats().add("Z1");
        assertEquals(1, selection.size());
        assertEquals(0, selection.clicks(), "pułapka: zmiana przez delegata omija licznik");
    }

    @Test
    void solutionIsNarrowFacadeWithDefensiveCopy() {
        var selection = new pl.training.workshop.m5.s08_composition.step2.SeatSelection();
        selection.addAll(List.of("H8", "H7"));
        assertEquals(List.of("H8", "H7"), selection.seats());
        assertThrows(UnsupportedOperationException.class, () -> selection.seats().add("Z1"));
        assertFalse(Set.class.isAssignableFrom(pl.training.workshop.m5.s08_composition.step2.SeatSelection.class), "świadomie: to już nie jest Set");
    }
}
