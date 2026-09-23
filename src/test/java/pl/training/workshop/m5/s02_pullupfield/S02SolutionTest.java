package pl.training.workshop.m5.s02_pullupfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

/** Pull Up Field: jedno pole private final w bazie, a pola o innym znaczeniu zostają na miejscu. */
final class S02SolutionTest {
    @Test
    void beforePullUpAllSeatFieldsHaveSameNameTypeAndLifecycle() throws Exception {
        for (Class<?> type : new Class<?>[] {pl.training.workshop.m5.s02_pullupfield.step2.StandardTicket.class,
                pl.training.workshop.m5.s02_pullupfield.step2.StudentTicket.class, pl.training.workshop.m5.s02_pullupfield.step2.VipTicket.class}) {
            Field seat = type.getDeclaredField("seat");
            assertEquals(String.class, seat.getType());
            assertTrue(Modifier.isPrivate(seat.getModifiers()) && Modifier.isFinal(seat.getModifiers()));
        }
        assertFalse(Modifier.isFinal(pl.training.workshop.m5.s02_pullupfield.step1.StandardTicket.class.getDeclaredField("seat").getModifiers()),
                "przed krokiem 2: inny cykl życia (setter)");
    }

    @Test
    void solutionDeclaresSeatOnceAsPrivateFinal() throws Exception {
        Field seat = pl.training.workshop.m5.s02_pullupfield.step3.Ticket.class.getDeclaredField("seat");
        assertTrue(Modifier.isPrivate(seat.getModifiers()));
        assertTrue(Modifier.isFinal(seat.getModifiers()));
        assertThrows(NoSuchFieldException.class, () -> pl.training.workshop.m5.s02_pullupfield.step3.VipTicket.class.getDeclaredField("seat"));
        assertEquals(String.class, pl.training.workshop.m5.s02_pullupfield.step3.StudentTicket.class.getDeclaredField("studentId").getType());
    }

    @Test
    void solutionReadsSeatThroughBaseType() {
        pl.training.workshop.m5.s02_pullupfield.step3.Ticket ticket = new pl.training.workshop.m5.s02_pullupfield.step3.VipTicket("k12");
        assertEquals("K12", ticket.seat());
    }
}
