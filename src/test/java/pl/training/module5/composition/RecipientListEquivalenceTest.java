package pl.training.module5.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module5.composition.after.RecipientList;

final class RecipientListEquivalenceTest {
    @Test
    void compositionPreservesTheIntendedRecipientListContract() {
        var before = new pl.training.module5.composition.before
                .RecipientList();
        var after = new RecipientList();

        assertEquals(
                before.add("alice@example.com"),
                after.add("alice@example.com"));
        assertEquals(
                before.add("bob@example.com"),
                after.add("bob@example.com"));
        assertEquals(
                before.add("alice@example.com"),
                after.add("alice@example.com"));

        assertEquals(3, before.size());
        assertEquals(before.size(), after.size());
        assertTrue(before.contains("bob@example.com"));
        assertEquals(
                before.contains("bob@example.com"),
                after.contains("bob@example.com"));
        assertIterableEquals(before, after);

        assertEquals(
                before.remove("alice@example.com"),
                after.remove("alice@example.com"));
        assertEquals(
                List.of("bob@example.com", "alice@example.com"),
                before.snapshot());
        assertEquals(before.snapshot(), after.snapshot());

        List<String> beforeSnapshot = before.snapshot();
        List<String> afterSnapshot = after.snapshot();
        assertThrows(
                UnsupportedOperationException.class,
                () -> beforeSnapshot.add("forbidden@example.com"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> afterSnapshot.add("forbidden@example.com"));
        before.add("carol@example.com");
        after.add("carol@example.com");

        assertEquals(
                List.of("bob@example.com", "alice@example.com"),
                beforeSnapshot);
        assertEquals(beforeSnapshot, afterSnapshot);
        assertIterableEquals(before, after);
        var afterIterator = after.iterator();
        afterIterator.next();
        assertThrows(
                UnsupportedOperationException.class,
                afterIterator::remove);
        boolean removedBefore = before.remove("missing@example.com");
        boolean removedAfter = after.remove("missing@example.com");
        assertFalse(removedBefore);
        assertEquals(removedBefore, removedAfter);

        var anotherBefore = new pl.training.module5.composition.before
                .RecipientList();
        var anotherAfter = new pl.training.module5.composition.after
                .RecipientList();
        assertEquals(0, anotherBefore.size());
        assertEquals(0, anotherAfter.size());
    }
}
