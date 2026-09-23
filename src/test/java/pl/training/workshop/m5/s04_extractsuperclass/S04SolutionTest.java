package pl.training.workshop.m5.s04_extractsuperclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Extract Superclass zmienia model typów: getSuperclass, typ deklarujący akcesorów, wspólny kontrakt. */
final class S04SolutionTest {
    private static final LocalDateTime EVENING = LocalDateTime.parse("2026-10-02T20:00");

    @Test
    void classesJoinTheSuperclassOneAtATime() {
        assertEquals(pl.training.workshop.m5.s04_extractsuperclass.step1.HallBooking.class, pl.training.workshop.m5.s04_extractsuperclass.step1.Screening.class.getSuperclass());
        assertEquals(Object.class, pl.training.workshop.m5.s04_extractsuperclass.step1.PrivateEvent.class.getSuperclass());
    }

    @Test
    void solutionSharesAbstractBookingWithDomainName() throws Exception {
        assertEquals(pl.training.workshop.m5.s04_extractsuperclass.step3.HallBooking.class, pl.training.workshop.m5.s04_extractsuperclass.step3.Screening.class.getSuperclass());
        assertEquals(pl.training.workshop.m5.s04_extractsuperclass.step3.HallBooking.class, pl.training.workshop.m5.s04_extractsuperclass.step3.PrivateEvent.class.getSuperclass());
        assertTrue(Modifier.isAbstract(pl.training.workshop.m5.s04_extractsuperclass.step3.HallBooking.class.getModifiers()));
        assertEquals(pl.training.workshop.m5.s04_extractsuperclass.step3.HallBooking.class,
                pl.training.workshop.m5.s04_extractsuperclass.step3.PrivateEvent.class.getMethod("hall").getDeclaringClass());
    }

    @Test
    void overlapsIsSymmetricAcrossBookingTypes() {
        pl.training.workshop.m5.s04_extractsuperclass.step3.HallBooking screening = new pl.training.workshop.m5.s04_extractsuperclass.step3.Screening("Diuna", "Sala 1", EVENING.minusHours(2), 166);
        pl.training.workshop.m5.s04_extractsuperclass.step3.HallBooking rental = pl.training.workshop.m5.s04_extractsuperclass.step3.PrivateEvent.rental("Firma X", "Sala 1", EVENING);
        assertTrue(screening.overlaps(rental));
        assertTrue(rental.overlaps(screening));
        assertFalse(rental.overlaps(pl.training.workshop.m5.s04_extractsuperclass.step3.PrivateEvent.rental("Firma Y", "Sala 1", EVENING.plusHours(2))));
    }
}
