package pl.training.workshop.m7.s06_parameterobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m7.s06_parameterobject.S06EquivalenceTest.Clump;

/**
 * Krok 3 przesuwa moment walidacji: wyjątek leci przy tworzeniu ScreeningSlot,
 * zanim wywołamy jakąkolwiek metodę. describe() dla złej sali przestaje działać.
 */
final class S06ValidationMomentTest {
    private static final Clump HALL_12 = new Clump("S1", S06EquivalenceTest.DAY, 12, "2D");
    private static final Clump FORMAT_4DX = new Clump("S1", S06EquivalenceTest.DAY, 3, "4DX");

    @Test
    void untilStep2OnlyTicketPriceRejectsWrongHall() {
        String expected = "S1 2026-03-10 sala 12 (2D) | EXC nie ma sali 12 (S1)";
        assertEquals(expected, S06EquivalenceTest.start(HALL_12));
        assertEquals(expected, S06EquivalenceTest.step1(HALL_12));
        assertEquals(expected, S06EquivalenceTest.step2(HALL_12));
    }

    @Test
    void step3RejectsWrongHallWhenTheSlotIsCreated() {
        assertEquals("new ScreeningSlot -> EXC nie ma sali 12 (S1)", S06EquivalenceTest.step3(HALL_12));
    }

    @Test
    void unknownFormatMovesTheSameWay() {
        assertEquals("S1 2026-03-10 sala 3 (4DX) | EXC nieznany format 4DX (S1)",
                S06EquivalenceTest.start(FORMAT_4DX));
        assertEquals("S1 2026-03-10 sala 3 (4DX) | EXC nieznany format 4DX (S1)",
                S06EquivalenceTest.step2(FORMAT_4DX));
        assertEquals("new ScreeningSlot -> EXC nieznany format 4DX (S1)",
                S06EquivalenceTest.step3(FORMAT_4DX));
    }
}
