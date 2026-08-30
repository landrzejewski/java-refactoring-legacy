package pl.training.module6.observer.after;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReleasePublisher {
    private final CopyOnWriteArrayList<Registration> listeners =
            new CopyOnWriteArrayList<>();

    public Subscription subscribe(ReleaseListener listener) {
        Registration registration = new Registration(
                Objects.requireNonNull(listener, "listener"));
        listeners.add(registration);
        AtomicBoolean active = new AtomicBoolean(true);
        return () -> {
            if (active.compareAndSet(true, false)) {
                listeners.remove(registration);
            }
        };
    }

    public void publish(ReleasePublished event) {
        Objects.requireNonNull(event, "event");
        for (Registration registration : listeners) {
            registration.notifyListener(event);
        }
    }

    private static final class Registration {
        private final ReleaseListener listener;

        private Registration(ReleaseListener listener) {
            this.listener = listener;
        }

        private void notifyListener(ReleasePublished event) {
            listener.onReleasePublished(event);
        }
    }
}
