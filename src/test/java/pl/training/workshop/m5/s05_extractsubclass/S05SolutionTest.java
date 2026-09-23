package pl.training.workshop.m5.s05_extractsubclass;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Extract Subclass celowo zmienia klasę runtime - to widzą getClass(), equals, ORM, JSON i switch. */
final class S05SolutionTest {
    @Test
    void beforeExtractionFactoriesReturnOneClass() {
        var premiere = pl.training.workshop.m5.s05_extractsubclass.step1.Screening.premiere("Amator", "2D", "Anna Nowak");
        var regular = pl.training.workshop.m5.s05_extractsubclass.step1.Screening.regular("Amator", "2D");
        assertEquals(regular.getClass(), premiere.getClass());
    }

    @Test
    void solutionPicksRuntimeClassInFactory() {
        assertEquals(pl.training.workshop.m5.s05_extractsubclass.step4.PremiereScreening.class,
                pl.training.workshop.m5.s05_extractsubclass.step4.Screening.premiere("Amator", "2D", "Anna Nowak").getClass());
        assertEquals(pl.training.workshop.m5.s05_extractsubclass.step4.Screening.class, pl.training.workshop.m5.s05_extractsubclass.step4.Screening.regular("Amator", "2D").getClass());
    }

    @Test
    void solutionKeepsPremiereStateOnlyInSubclass() {
        assertThrows(NoSuchFieldException.class, () -> pl.training.workshop.m5.s05_extractsubclass.step4.Screening.class.getDeclaredField("guest"));
        assertThrows(NoSuchFieldException.class, () -> pl.training.workshop.m5.s05_extractsubclass.step4.Screening.class.getDeclaredField("premiere"));
        assertArrayEquals(new Class<?>[] {pl.training.workshop.m5.s05_extractsubclass.step4.PremiereScreening.class},
                pl.training.workshop.m5.s05_extractsubclass.step4.Screening.class.getPermittedSubclasses());
    }
}
