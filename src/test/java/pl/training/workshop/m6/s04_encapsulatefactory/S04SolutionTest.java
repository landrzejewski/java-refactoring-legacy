package pl.training.workshop.m6.s04_encapsulatefactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

/** Po kroku 3 publiczne są tylko Ticket i fabryka Tickets. */
final class S04SolutionTest {
    private static final String STEP3 = "pl.training.workshop.m6.s04_encapsulatefactory.step3.tickets.";

    @Test
    void solutionHidesConcreteTicketClassesBehindTheFactory() throws ClassNotFoundException {
        assertFalse(Modifier.isPublic(Class.forName(STEP3 + "StandardTicket").getModifiers()));
        assertFalse(Modifier.isPublic(Class.forName(STEP3 + "VipTicket").getModifiers()));
        assertTrue(Modifier.isPublic(Class.forName(STEP3 + "Tickets").getModifiers()));
        assertTrue(Modifier.isPublic(Class.forName(STEP3 + "Ticket").getModifiers()));
    }
}
