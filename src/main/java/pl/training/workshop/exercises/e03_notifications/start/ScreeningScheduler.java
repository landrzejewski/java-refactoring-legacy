package pl.training.workshop.exercises.e03_notifications.start;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/**
 * Start: zmiana repertuaru sama wie, kogo i jak powiadomic. Po kazdej zmianie scheduler na sztywno
 * wola kolejno: powiadomienia klientow, liste oczekujacych i statystyki. Wybor kanalu (push, potem SMS,
 * potem e-mail) jest lancuchem if skopiowanym do obu metod, a kazda para kanal x rodzaj to osobna klasa.
 * Nowy odbiorca zmian (np. kasa biletowa) albo nowy kanal to edycja tej klasy w dwoch miejscach.
 */
public final class ScreeningScheduler {
    private final Journal journal;
    private final Waitlist waitlist;
    private final ChangeStats stats;
    private final Map<String, Screening> current = new HashMap<>();
    private final Set<String> cancelled = new HashSet<>();

    public ScreeningScheduler(Journal journal, Waitlist waitlist, ChangeStats stats) {
        this.journal = journal;
        this.waitlist = waitlist;
        this.stats = stats;
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
        for (Customer customer : ticketHolders) {
            if (customer.pushToken() != null && !customer.pushToken().isBlank()
                    && !customer.pushToken().startsWith("expired-")) {
                new PushMovedNotice(journal, before, after).send(customer);
            } else if (customer.phone() != null && !customer.phone().isBlank()) {
                new SmsMovedNotice(journal, before, after).send(customer);
            } else if (customer.email() != null && !customer.email().isBlank()) {
                new EmailMovedNotice(journal, before, after).send(customer);
            } else {
                journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
            }
        }
        waitlist.screeningMoved(after);
        stats.moved();
    }

    public void cancel(String screeningId, String reason, List<Customer> ticketHolders) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        Screening screening = live(screeningId);
        cancelled.add(screeningId);
        journal.note("SCHEDULE " + screening.title() + " " + screening.start() + " odwolany: " + reason);
        for (Customer customer : ticketHolders) {
            if (customer.pushToken() != null && !customer.pushToken().isBlank()
                    && !customer.pushToken().startsWith("expired-")) {
                new PushCancelledNotice(journal, screening, reason).send(customer);
            } else if (customer.phone() != null && !customer.phone().isBlank()) {
                new SmsCancelledNotice(journal, screening, reason).send(customer);
            } else if (customer.email() != null && !customer.email().isBlank()) {
                new EmailCancelledNotice(journal, screening, reason).send(customer);
            } else {
                journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
            }
        }
        waitlist.screeningCancelled(screening);
        stats.cancelled();
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
