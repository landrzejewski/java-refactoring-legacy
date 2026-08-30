package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import pl.training.module7.duplication.after.ArtifactPublisher;
import pl.training.module7.duplication.before.LegacyArtifactPublisher;

final class DuplicationEquivalenceTest {
    @Test
    @ResourceLock(Resources.LOCALE)
    void preservesBothPublicationVariantsIndependentlyOfDefaultLocale() {
        Locale previousLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            var legacy = new LegacyArtifactPublisher();
            var refactored = new ArtifactPublisher();

            assertEquals("image:42-SNAPSHOT", legacy.publishSnapshot(" IMAGE ", 42));
            assertEquals(
                    legacy.publishSnapshot(" IMAGE ", 42),
                    refactored.publishSnapshot(" IMAGE ", 42));
            assertEquals("image:42", legacy.publishRelease(" IMAGE ", 42));
            assertEquals(
                    legacy.publishRelease(" IMAGE ", 42),
                    refactored.publishRelease(" IMAGE ", 42));
        } finally {
            Locale.setDefault(previousLocale);
        }
    }

    @Test
    void preservesValidationTypeMessageAndOrderForBothVariants() {
        var legacy = new LegacyArtifactPublisher();
        var refactored = new ArtifactPublisher();

        assertSameFailure(
                () -> legacy.publishSnapshot(null, 0),
                () -> refactored.publishSnapshot(null, 0));
        assertSameFailure(
                () -> legacy.publishSnapshot(" \t", 0),
                () -> refactored.publishSnapshot(" \t", 0));
        assertSameFailure(
                () -> legacy.publishSnapshot("api", 0),
                () -> refactored.publishSnapshot("api", 0));

        assertSameFailure(
                () -> legacy.publishRelease(null, 0),
                () -> refactored.publishRelease(null, 0));
        assertSameFailure(
                () -> legacy.publishRelease(" \t", 0),
                () -> refactored.publishRelease(" \t", 0));
        assertSameFailure(
                () -> legacy.publishRelease("api", 0),
                () -> refactored.publishRelease("api", 0));
    }

    private static void assertSameFailure(
            Supplier<String> legacyCall,
            Supplier<String> refactoredCall) {

        RuntimeException legacyFailure = assertThrows(
                RuntimeException.class,
                legacyCall::get);
        RuntimeException refactoredFailure = assertThrows(
                RuntimeException.class,
                refactoredCall::get);

        assertEquals(legacyFailure.getClass(), refactoredFailure.getClass());
        assertEquals(legacyFailure.getMessage(), refactoredFailure.getMessage());
    }
}
