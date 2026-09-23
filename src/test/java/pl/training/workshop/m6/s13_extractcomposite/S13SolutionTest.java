package pl.training.workshop.m6.s13_extractcomposite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s13_extractcomposite.step2.CompositeProgramItem;
import pl.training.workshop.m6.s13_extractcomposite.step2.Marathon;
import pl.training.workshop.m6.s13_extractcomposite.step2.ShortsBlock;

/** Wspólna nadklasa pilnuje kontraktu dzieci raz dla wszystkich kontenerów. */
final class S13SolutionTest {
    @Test
    void everyContainerRejectsNullChild() {
        for (CompositeProgramItem container : new CompositeProgramItem[] {new Marathon("M"), new ShortsBlock("B")}) {
            assertThrows(NullPointerException.class, () -> container.add(null));
        }
    }

    @Test
    void childrenViewIsACopy() {
        Marathon marathon = new Marathon("M");
        assertThrows(UnsupportedOperationException.class, () -> marathon.children().add(null));
        assertEquals(0, marathon.minutes());
    }
}
