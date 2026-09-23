package pl.training.workshop.m5.s01_pullupmethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Po Pull Up zmienia się typ deklarujący metody - to widzi refleksja i każdy stary .class. */
final class S01SolutionTest {
    @Test
    void beforeLastStepEverySubclassDeclaresLabel() {
        assertFalse(declares(pl.training.workshop.m5.s01_pullupmethod.step2.Ticket.class, "label"));
        assertTrue(declares(pl.training.workshop.m5.s01_pullupmethod.step2.StandardTicket.class, "label"));
        assertTrue(declares(pl.training.workshop.m5.s01_pullupmethod.step2.StudentTicket.class, "label"));
        assertTrue(declares(pl.training.workshop.m5.s01_pullupmethod.step2.VipTicket.class, "label"));
    }

    @Test
    void solutionDeclaresLabelOnceAsFinalAndPriceAsAbstract() throws Exception {
        Method label = pl.training.workshop.m5.s01_pullupmethod.step3.Ticket.class.getDeclaredMethod("label");
        Method price = pl.training.workshop.m5.s01_pullupmethod.step3.Ticket.class.getDeclaredMethod("price");
        assertTrue(Modifier.isFinal(label.getModifiers()));
        assertTrue(Modifier.isAbstract(price.getModifiers()));
        assertFalse(declares(pl.training.workshop.m5.s01_pullupmethod.step3.StandardTicket.class, "label"));
        assertFalse(declares(pl.training.workshop.m5.s01_pullupmethod.step3.StudentTicket.class, "label"));
        assertFalse(declares(pl.training.workshop.m5.s01_pullupmethod.step3.VipTicket.class, "label"));
    }

    @Test
    void solutionIsUsableThroughBaseType() {
        pl.training.workshop.m5.s01_pullupmethod.step3.Ticket ticket = new pl.training.workshop.m5.s01_pullupmethod.step3.StudentTicket("Amator", Money.of("25.00"));
        assertEquals("Amator: 18.75", ticket.label());
    }

    private static boolean declares(Class<?> type, String name) {
        return Arrays.stream(type.getDeclaredMethods()).anyMatch(m -> m.getName().equals(name));
    }
}
