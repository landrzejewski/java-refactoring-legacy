package pl.training.workshop.m5.s07_collapsehierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/** Collapse Hierarchy: zbędny poziom znika, nazwa używana przez klientów (Hall) zostaje. */
final class S07SolutionTest {
    @Test
    void beforeCollapseSubclassHasNoOwnStateOrBehaviour() {
        Class<?> imaxHall = pl.training.workshop.m5.s07_collapsehierarchy.step2.ImaxHall.class;
        // pomijamy składowe syntetyczne (np. $jacocoInit dodawane przez JaCoCo pod Mavenem)
        assertEquals(0, Arrays.stream(imaxHall.getDeclaredFields()).filter(f -> !f.isSynthetic()).count());
        assertEquals(0, Arrays.stream(imaxHall.getDeclaredMethods()).filter(m -> !m.isSynthetic()).count());
    }

    @Test
    void solutionHasSingleFinalClass() {
        assertTrue(Modifier.isFinal(pl.training.workshop.m5.s07_collapsehierarchy.step3.Hall.class.getModifiers()));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("pl.training.workshop.m5.s07_collapsehierarchy.step3.ImaxHall"));
        assertEquals(pl.training.workshop.m5.s07_collapsehierarchy.step3.Hall.class, pl.training.workshop.m5.s07_collapsehierarchy.step3.Hall.imax("Sala IMAX", 14, 22).getClass());
    }
}
