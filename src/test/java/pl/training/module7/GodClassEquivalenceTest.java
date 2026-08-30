package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module7.godclass.PublishedRelease;
import pl.training.module7.godclass.after.AuditTrail;
import pl.training.module7.godclass.after.InMemoryReleaseRepository;
import pl.training.module7.godclass.after.ReleaseApplicationService;
import pl.training.module7.godclass.after.ReleaseNotifier;
import pl.training.module7.godclass.after.ReleaseValidator;
import pl.training.module7.godclass.before.LegacyReleaseManager;

final class GodClassEquivalenceTest {
    @Test
    void collaboratorsPreserveResultEffectsAndTheirOrder() {
        var before = new LegacyReleaseManager();
        List<String> events = new ArrayList<>();
        var repository = new InMemoryReleaseRepository(events::add);
        var auditTrail = new AuditTrail(events::add);
        var notifier = new ReleaseNotifier(events::add);
        var after = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);

        PublishedRelease legacyResult = before.publish(
                "rel-42", "payments", "2.1.0");
        PublishedRelease refactoredResult = after.publish(
                "rel-42", "payments", "2.1.0");

        assertEquals(legacyResult, refactoredResult);
        assertEquals(before.releases(), repository.findAll());
        assertEquals(before.auditEntries(), auditTrail.entries());
        assertEquals(before.notifications(), notifier.notifications());
        assertEquals(
                List.of("save:rel-42", "audit:rel-42", "notify:rel-42"),
                events);
        assertEquals(before.events(), events);
    }

    @Test
    void validationAndDuplicateFailuresDoNotCreateFurtherEffects() {
        var before = new LegacyReleaseManager();
        List<String> events = new ArrayList<>();
        var repository = new InMemoryReleaseRepository(events::add);
        var auditTrail = new AuditTrail(events::add);
        var notifier = new ReleaseNotifier(events::add);
        var after = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);

        RuntimeException legacyValidationFailure = assertThrows(
                RuntimeException.class,
                () -> before.publish(" ", "payments", "2.1.0"));
        RuntimeException refactoredValidationFailure = assertThrows(
                RuntimeException.class,
                () -> after.publish(" ", "payments", "2.1.0"));
        assertSameFailure(
                legacyValidationFailure,
                refactoredValidationFailure);
        assertNoAfterEffects(repository, auditTrail, notifier, events);
        assertEquals(List.of(), before.events());

        before.publish("rel-42", "payments", "2.1.0");
        after.publish("rel-42", "payments", "2.1.0");
        List<String> eventsBeforeDuplicate = List.copyOf(events);
        RuntimeException legacyDuplicateFailure = assertThrows(
                RuntimeException.class,
                () -> before.publish("rel-42", "other", "9.0.0"));
        RuntimeException refactoredDuplicateFailure = assertThrows(
                RuntimeException.class,
                () -> after.publish("rel-42", "other", "9.0.0"));

        assertSameFailure(legacyDuplicateFailure, refactoredDuplicateFailure);
        assertEquals(eventsBeforeDuplicate, events);
        assertEquals(1, repository.findAll().size());
        assertEquals(1, auditTrail.entries().size());
        assertEquals(1, notifier.notifications().size());
        assertEquals(before.releases(), repository.findAll());
        assertEquals(before.auditEntries(), auditTrail.entries());
        assertEquals(before.notifications(), notifier.notifications());
    }

    @Test
    void exposedCollectionsAreDefensiveSnapshots() {
        var before = new LegacyReleaseManager();
        before.publish("rel-42", "payments", "2.1.0");
        var repository = new InMemoryReleaseRepository();
        var auditTrail = new AuditTrail();
        var notifier = new ReleaseNotifier();
        var after = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);
        after.publish("rel-42", "payments", "2.1.0");

        assertThrows(
                UnsupportedOperationException.class,
                () -> before.releases().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> before.auditEntries().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> before.notifications().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> repository.findAll().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> auditTrail.entries().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> notifier.notifications().clear());
    }

    @Test
    void repositoryFailureStopsLaterEffectsAndKeepsTheCompletedSave() {
        List<String> events = new ArrayList<>();
        var repository = new InMemoryReleaseRepository(event -> {
            events.add(event);
            throw new IllegalStateException("repository event failure");
        });
        var auditTrail = new AuditTrail(events::add);
        var notifier = new ReleaseNotifier(events::add);
        var service = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> service.publish("rel-42", "payments", "2.1.0"));

        assertEquals("repository event failure", failure.getMessage());
        assertEquals(List.of("save:rel-42"), events);
        assertEquals(1, repository.findAll().size());
        assertEquals(List.of(), auditTrail.entries());
        assertEquals(List.of(), notifier.notifications());
    }

    @Test
    void auditFailureStopsNotificationAndKeepsEarlierEffects() {
        List<String> events = new ArrayList<>();
        var repository = new InMemoryReleaseRepository(events::add);
        var auditTrail = new AuditTrail(event -> {
            events.add(event);
            throw new IllegalStateException("audit event failure");
        });
        var notifier = new ReleaseNotifier(events::add);
        var service = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> service.publish("rel-42", "payments", "2.1.0"));

        assertEquals("audit event failure", failure.getMessage());
        assertEquals(List.of("save:rel-42", "audit:rel-42"), events);
        assertEquals(1, repository.findAll().size());
        assertEquals(List.of("published:rel-42"), auditTrail.entries());
        assertEquals(List.of(), notifier.notifications());
    }

    @Test
    void notificationFailureKeepsAllEarlierEffects() {
        List<String> events = new ArrayList<>();
        var repository = new InMemoryReleaseRepository(events::add);
        var auditTrail = new AuditTrail(events::add);
        var notifier = new ReleaseNotifier(event -> {
            events.add(event);
            throw new IllegalStateException("notification event failure");
        });
        var service = new ReleaseApplicationService(
                new ReleaseValidator(), repository, auditTrail, notifier);

        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> service.publish("rel-42", "payments", "2.1.0"));

        assertEquals("notification event failure", failure.getMessage());
        assertEquals(List.of(
                "save:rel-42", "audit:rel-42", "notify:rel-42"), events);
        assertEquals(1, repository.findAll().size());
        assertEquals(List.of("published:rel-42"), auditTrail.entries());
        assertEquals(
                List.of("release-published:rel-42"),
                notifier.notifications());
    }

    private static void assertNoAfterEffects(
            InMemoryReleaseRepository repository,
            AuditTrail auditTrail,
            ReleaseNotifier notifier,
            List<String> events) {
        assertEquals(List.of(), repository.findAll());
        assertEquals(List.of(), auditTrail.entries());
        assertEquals(List.of(), notifier.notifications());
        assertEquals(List.of(), events);
    }

    private static void assertSameFailure(
            RuntimeException before,
            RuntimeException after) {
        assertEquals(before.getClass(), after.getClass());
        assertEquals(before.getMessage(), after.getMessage());
    }
}
