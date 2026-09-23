package pl.training.workshop.m5.s10_fieldhiding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

/** Pola i metody static są wiązane statycznie - wynik zależy od typu referencji, nie od obiektu. */
final class S10SolutionTest {
    /*
     * Testy start używają refleksji zamiast student.type / StudentTicket.category(), żeby cały moduł
     * kompilował się także po naprawie start na żywo (wtedy te dwa testy po prostu zrobią się czerwone).
     */
    @Test
    void startHidesFieldSoOneObjectHasTwoSlots() throws Exception {
        Object student = new pl.training.workshop.m5.s10_fieldhiding.start.StudentTicket();
        Field inStudentTicket = pl.training.workshop.m5.s10_fieldhiding.start.StudentTicket.class.getDeclaredField("type");
        Field inTicket = pl.training.workshop.m5.s10_fieldhiding.start.Ticket.class.getDeclaredField("type");
        assertEquals("STUDENT", inStudentTicket.get(student), "((StudentTicket) t).type");
        assertEquals("NORMAL", inTicket.get(student), "pułapka: ((Ticket) t).type - drugi slot w tym samym obiekcie");
    }

    @Test
    void startStaticMethodIsHiddenNotOverridden() throws Exception {
        Method hiding = pl.training.workshop.m5.s10_fieldhiding.start.StudentTicket.class.getDeclaredMethod("category");
        assertTrue(Modifier.isStatic(hiding.getModifiers()));
        assertEquals("BILET ULGOWY", hiding.invoke(null));
        assertEquals("BILET: NORMAL", new pl.training.workshop.m5.s10_fieldhiding.start.StudentTicket().label(), "pułapka: label() widzi pole i static bazy");
    }

    @Test
    void step1FixesFieldButStaticIsStillHidden() {
        assertEquals("BILET: STUDENT", new pl.training.workshop.m5.s10_fieldhiding.step1.StudentTicket().label());
        assertThrows(NoSuchFieldException.class, () -> pl.training.workshop.m5.s10_fieldhiding.step1.StudentTicket.class.getDeclaredField("type"));
    }

    @Test
    void solutionDispatchesOnObjectRegardlessOfReferenceType() {
        pl.training.workshop.m5.s10_fieldhiding.step2.Ticket ticket = new pl.training.workshop.m5.s10_fieldhiding.step2.StudentTicket();
        assertEquals("BILET ULGOWY: STUDENT", ticket.label());
        assertEquals("BILET ULGOWY", ticket.category());
    }
}
