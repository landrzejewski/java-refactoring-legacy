package pl.training.workshop.exercises.e03_notifications.step2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/**
 * Krok 2: scheduler zapisuje zmiane i publikuje zdarzenie do zarejestrowanych odbiorcow.
 * Kolejnosc powiadamiania to kolejnosc rejestracji - to czesc kontraktu (powiadomienia, lista, statystyki).
 * Wyjatek odbiorcy przerywa powiadamianie kolejnych - tak bylo w start i tak zostaje.
 */
public final class ScreeningScheduler {
    private final Journal journal;
    private final List<ScreeningListener> listeners = new ArrayList<>();
    private final Map<String, Screening> current = new HashMap<>();
    private final Set<String> cancelled = new HashSet<>();

    public ScreeningScheduler(Journal journal) {
        this.journal = journal;
    }

    public void subscribe(ScreeningListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(ScreeningListener listener) {
        listeners.remove(listener);
    }

    public void register(Screening screening) {
        current.put(screening.id(), screening);
    }

    public void move(String screeningId, String newStart, String newHall, List<Customer> ticketHolders) {
        if (newStart == null || newStart.isBlank()) {
            throw new IllegalArgumentException("new start must not be blank");
        }
        if (newHall == null || newHall.isBlank()) {
            throw new IllegalArgumentException("hall must not be blank");
        }
        Screening before = live(screeningId);
        Screening after = new Screening(before.id(), before.title(), newStart, newHall);
        current.put(screeningId, after);
        journal.note("SCHEDULE " + before.title() + " " + before.start() + " -> " + newStart + " sala " + newHall);
        ScreeningMoved event = new ScreeningMoved(before, after, List.copyOf(ticketHolders));
        for (ScreeningListener listener : List.copyOf(listeners)) {
            listener.screeningMoved(event);
        }
    }

    public void cancel(String screeningId, String reason, List<Customer> ticketHolders) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        Screening screening = live(screeningId);
        cancelled.add(screeningId);
        journal.note("SCHEDULE " + screening.title() + " " + screening.start() + " odwolany: " + reason);
        ScreeningCancelled event = new ScreeningCancelled(screening, reason, List.copyOf(ticketHolders));
        for (ScreeningListener listener : List.copyOf(listeners)) {
            listener.screeningCancelled(event);
        }
    }

    private Screening live(String screeningId) {
        Screening screening = current.get(screeningId);
        if (screening == null) {
            throw new IllegalArgumentException("unknown screening: " + screeningId);
        }
        if (cancelled.contains(screeningId)) {
            throw new IllegalArgumentException("screening already cancelled: " + screening.title());
        }
        return screening;
    }
}
