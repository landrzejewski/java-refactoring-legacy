package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module6.observer.after.ReleasePublished;
import pl.training.module6.observer.after.ReleasePublisher;
import pl.training.module6.observer.after.Subscription;
import pl.training.module6.observer.before.LegacyReleasePublisher;

final class ObserverContractTest {
    @Test
    void preservesSynchronousRegistrationOrderForExistingRecipients() {
        List<String> before = new ArrayList<>();
        List<String> after = new ArrayList<>();
        var legacy = new LegacyReleasePublisher(
                event -> before.add("audit:" + event.releaseId()),
                event -> before.add("metric:" + event.releaseId()));
        var publisher = new ReleasePublisher();
        publisher.subscribe(event -> after.add("audit:" + event.releaseId()));
        publisher.subscribe(event -> after.add("metric:" + event.releaseId()));

        legacy.publish(new LegacyReleasePublisher.PublishedRelease("rel-42"));
        publisher.publish(new ReleasePublished("rel-42"));

        assertEquals(before, after);
    }

    @Test
    void unsubscriptionDuringPublicationAffectsTheNextSnapshot() {
        List<String> calls = new ArrayList<>();
        var publisher = new ReleasePublisher();
        Subscription[] later = new Subscription[1];
        publisher.subscribe(event -> {
            calls.add("first:" + event.releaseId());
            later[0].close();
        });
        later[0] = publisher.subscribe(
                event -> calls.add("later:" + event.releaseId()));

        publisher.publish(new ReleasePublished("one"));
        publisher.publish(new ReleasePublished("two"));

        assertEquals(List.of("first:one", "later:one", "first:two"), calls);
    }

    @Test
    void usesFailFastExceptionPolicy() {
        List<String> calls = new ArrayList<>();
        RuntimeException failure = new IllegalStateException("listener failed");
        var publisher = new ReleasePublisher();
        publisher.subscribe(event -> {
            throw failure;
        });
        publisher.subscribe(event -> calls.add("not-reached"));

        RuntimeException propagated = assertThrows(
                RuntimeException.class,
                () -> publisher.publish(new ReleasePublished("rel-42")));

        assertSame(failure, propagated);
        assertEquals(List.of(), calls);
    }

    @Test
    void duplicateRegistrationsRemainIndependentSubscriptions() {
        var publisher = new ReleasePublisher();
        List<String> calls = new ArrayList<>();
        pl.training.module6.observer.after.ReleaseListener listener =
                event -> calls.add("duplicate");
        Subscription first = publisher.subscribe(listener);
        Subscription middle = publisher.subscribe(event -> calls.add("middle"));
        Subscription second = publisher.subscribe(listener);

        second.close();
        publisher.publish(new ReleasePublished("one"));
        first.close();
        middle.close();
        publisher.publish(new ReleasePublished("two"));

        assertEquals(List.of("duplicate", "middle"), calls);
    }
}
