package pl.training.workshop.m4.s08_movefield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/** Po co Move Field: w start dwa seanse w TEJ SAMEJ sali mogą się nie zgadzać co do VIP. */
final class S08SingleSourceOfTruthTest {
    @Test
    void startLetsScreeningsInOneHallDisagree() {
        var hall = new pl.training.workshop.m4.s08_movefield.start.Hall("Sala 1");
        var evening = new pl.training.workshop.m4.s08_movefield.start.Screening(hall, 3, 10);
        var morning = new pl.training.workshop.m4.s08_movefield.start.Screening(hall, 1, 8);
        assertNotEquals(evening.isVip(9), morning.isVip(9), "ten sam fotel raz jest VIP, raz nie");
    }

    @Test
    void afterMoveFieldTheHallDecidesForEveryScreening() {
        var hall = new pl.training.workshop.m4.s08_movefield.step3.Hall("Sala 1", 10);
        var evening = new pl.training.workshop.m4.s08_movefield.step3.Screening(hall, 3);
        var morning = new pl.training.workshop.m4.s08_movefield.step3.Screening(hall, 1);
        assertEquals(evening.isVip(9), morning.isVip(9));
        assertEquals(evening.isVip(10), morning.isVip(10));
    }
}
