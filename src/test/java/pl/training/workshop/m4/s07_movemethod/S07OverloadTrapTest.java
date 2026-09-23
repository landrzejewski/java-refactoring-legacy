package pl.training.workshop.m4.s07_movemethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Dokumentuje pułapkę typu docelowego: przy przenoszeniu ktoś "sprząta" parametr Integer -> int,
 * bo w nowym właścicielu wszystkie numery są prymitywami. Kod się kompiluje, ale wybiera inne przeciążenie.
 */
final class S07OverloadTrapTest {
    @Test
    void integerParameterRemovesTheSeatNumber() {
        assertEquals(List.of(2, 3, 4, 5), new pl.training.workshop.m4.s07_movemethod.step2.Screening(
                "Diuna", 3, S07EquivalenceTest.DIUNA.start(), 1, List.of(1, 2, 3, 4, 5)).freeSeatsWithout(1));
    }

    @Test
    void intParameterRemovesTheElementAtIndex() {
        assertEquals(List.of(1, 3, 4, 5), naiveFreeSeatsWithout(List.of(1, 2, 3, 4, 5), 1),
                "remove(int) usunął miejsce nr 2, a zostawił zarezerwowane nr 1");
        assertThrows(IndexOutOfBoundsException.class, () -> naiveFreeSeatsWithout(List.of(7, 8, 9), 8));
    }

    /** Tak wyglądałoby Screening.freeSeatsWithout po "sprzątaniu" typu parametru. */
    private static List<Integer> naiveFreeSeatsWithout(List<Integer> freeSeats, int seat) {
        List<Integer> free = new ArrayList<>(freeSeats);
        free.remove(seat);
        return free;
    }
}
