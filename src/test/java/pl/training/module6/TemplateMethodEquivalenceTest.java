package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import pl.training.module6.templatemethod.after.KeyValueReleaseImporter;
import pl.training.module6.templatemethod.after.PipeReleaseImporter;
import pl.training.module6.templatemethod.after.ReleaseDraft;
import pl.training.module6.templatemethod.after.ReleaseImporter;
import pl.training.module6.templatemethod.before.LegacyKeyValueReleaseImporter;
import pl.training.module6.templatemethod.before.LegacyPipeReleaseImporter;

final class TemplateMethodEquivalenceTest {
    @Test
    void commonSkeletonPreservesBothImportFormats() {
        var legacyPipe = new LegacyPipeReleaseImporter()
                .importRelease(" rel-42 | payments ");
        var pipe = new PipeReleaseImporter()
                .importRelease(" rel-42 | payments ");
        var legacyKeyValue = new LegacyKeyValueReleaseImporter()
                .importRelease("id=rel-42;service=payments");
        var keyValue = new KeyValueReleaseImporter()
                .importRelease("id=rel-42;service=payments");

        assertEquivalent(legacyPipe, pipe);
        assertEquivalent(legacyKeyValue, keyValue);
    }

    @Test
    void templateMethodProtectsTheRequiredOrder() throws NoSuchMethodException {
        int modifiers = ReleaseImporter.class
                .getDeclaredMethod("importRelease", String.class)
                .getModifiers();

        assertTrue(Modifier.isFinal(modifiers));
    }

    @Test
    void commonValidationPreservesTheLegacyFailure() {
        RuntimeException before = assertThrows(
                RuntimeException.class,
                () -> new LegacyPipeReleaseImporter().importRelease(" |payments"));
        RuntimeException after = assertThrows(
                RuntimeException.class,
                () -> new PipeReleaseImporter().importRelease(" |payments"));

        assertEquals(before.getClass(), after.getClass());
        assertEquals(before.getMessage(), after.getMessage());
    }

    @Test
    void supportsSubclassOutsideTheImplementationPackage() {
        ReleaseDraft imported = new CommaReleaseImporter()
                .importRelease("rel-42,payments");

        assertEquals("rel-42", imported.releaseId());
        assertEquals("payments", imported.service());
    }

    private static void assertEquivalent(
            pl.training.module6.templatemethod.before.ReleaseDraft before,
            ReleaseDraft after) {
        assertEquals(before.releaseId(), after.releaseId());
        assertEquals(before.service(), after.service());
    }

    private static final class CommaReleaseImporter extends ReleaseImporter {
        @Override
        protected Fields parse(String raw) {
            String[] parts = raw.split(",", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException(
                        "expected releaseId and service");
            }
            return fields(parts[0].trim(), parts[1].trim());
        }
    }
}
