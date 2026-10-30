package pl.training.workshop.exercises.e03_notifications.start;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

import java.util.*;

/**
 * Start: zmiana repertuaru sama wie, kogo i jak powiadomic. Po kazdej zmianie scheduler na sztywno
 * wola kolejno: powiadomienia klientow, liste oczekujacych i statystyki. Wybor kanalu (push, potem SMS,
 * potem e-mail) jest lancuchem if skopiowanym do obu metod, a kazda para kanal x rodzaj to osobna klasa.
 * Nowy odbiorca zmian (np. kasa biletowa) albo nowy kanal to edycja tej klasy w dwoch miejscach.
 */
public final class ScreeningScheduler {
    private final Journal journal;
    private final Map<String, Screening> current = new HashMap<>();
    private final Set<String> cancelled = new HashSet<>();
    private final List<ScreeningListener> listeners = new ArrayList<>();

    public ScreeningScheduler(Journal journal) {
        this.journal = journal;
    }

    public void addListener(ScreeningListener listener) {
        listeners.add(listener);
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
        var movedEvent = new ScreeningMoved(before, after, ticketHolders);
        listeners.forEach(listener -> listener.onMoved(movedEvent));
    }

    public void cancel(String screeningId, String reason, List<Customer> ticketHolders) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        Screening screening = live(screeningId);
        cancelled.add(screeningId);
        journal.note("SCHEDULE " + screening.title() + " " + screening.start() + " odwolany: " + reason);
        var cancelledEvent = new ScreeningCancelled(screening, reason, ticketHolders);
        listeners.forEach(listener -> listener.onCanceled(cancelledEvent));
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
