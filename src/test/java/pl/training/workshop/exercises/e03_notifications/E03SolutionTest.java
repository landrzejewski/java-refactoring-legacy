package pl.training.workshop.exercises.e03_notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e03_notifications.step2.ChangeStats;
import pl.training.workshop.exercises.e03_notifications.step2.Channel;
import pl.training.workshop.exercises.e03_notifications.step2.CustomerNotifier;
import pl.training.workshop.exercises.e03_notifications.step2.EmailChannel;
import pl.training.workshop.exercises.e03_notifications.step2.EmailLink;
import pl.training.workshop.exercises.e03_notifications.step2.Journal;
import pl.training.workshop.exercises.e03_notifications.step2.PushChannel;
import pl.training.workshop.exercises.e03_notifications.step2.PushLink;
import pl.training.workshop.exercises.e03_notifications.step2.ScreeningCancelled;
import pl.training.workshop.exercises.e03_notifications.step2.ScreeningMoved;
import pl.training.workshop.exercises.e03_notifications.step2.ScreeningScheduler;
import pl.training.workshop.exercises.e03_notifications.step2.SmsLink;
import pl.training.workshop.exercises.e03_notifications.step2.UndeliveredLink;
import pl.training.workshop.exercises.e03_notifications.step2.Waitlist;

/**
 * Kontrakt rozwiazania: nadawca zna tylko interfejs odbiorcow, nowy kanal dziala z kazdym rodzajem wiadomosci,
 * a ogniwo dostarczania przekazuje dalej tylko to, czego samo nie obsluzylo.
 */
final class E03SolutionTest {
    private static final Screening DIUNA = new Screening("S1", "Diuna", "2026-10-30 18:00", "1");
    private static final Screening MOVED = new Screening("S1", "Diuna", "2026-10-31 20:00", "2");
    private static final Customer ANNA = new Customer("Anna", "anna@kino.pl", "600100200", "tok-anna");
    private static final Customer CELINA = new Customer("Celina", "celina@kino.pl", null, null);

    @Test
    void linkPassesOnOnlyWhatItCannotDeliver() {
        Journal journal = new Journal();
        AtomicInteger passedOn = new AtomicInteger();
        Channel spy = (customer, subject, body) -> passedOn.incrementAndGet();
        PushLink push = new PushLink(new PushChannel(journal), spy);

        push.deliver(ANNA, "tytul", "tresc");
        assertEquals(List.of("PUSH tok-anna | tytul | tresc"), journal.lines());
        assertEquals(0, passedOn.get(), "obsluzone zadanie nie idzie dalej");

        push.deliver(CELINA, "tytul", "tresc");
        assertEquals(1, passedOn.get(), "nieobsluzone zadanie idzie dokladnie do nastepnika");
        assertEquals(1, journal.lines().size(), "ogniwo bez kanalu nic nie wysyla");
    }

    @Test
    void chainIsAssembledExplicitlySoAFestivalCanUseEmailOnly() {
        Journal journal = new Journal();
        Channel emailOnly = new EmailLink(new EmailChannel(journal), new UndeliveredLink(journal));
        new CustomerNotifier(emailOnly).screeningMoved(new ScreeningMoved(DIUNA, MOVED, List.of(ANNA)));
        assertEquals(List.of("EMAIL anna@kino.pl | Zmiana seansu: Diuna | Seans Diuna z 2026-10-30 18:00 "
                + "przeniesiony na 2026-10-31 20:00, sala 2. Bilety zachowuja waznosc."), journal.lines());
    }

    @Test
    void everyLinkKnowsOnlyItsSuccessor() {
        for (Class<?> link : List.of(PushLink.class, SmsLink.class, EmailLink.class)) {
            long successors = Arrays.stream(link.getDeclaredFields())
                    .map(Field::getType)
                    .filter(Channel.class::equals)
                    .count();
            assertEquals(1, successors, link.getSimpleName());
        }
        long endSuccessors = Arrays.stream(UndeliveredLink.class.getDeclaredFields())
                .map(Field::getType)
                .filter(Channel.class::equals)
                .count();
        assertEquals(0, endSuccessors, "koniec lancucha nie ma nastepnika");
    }

    @Test
    void newChannelIsOneClassWorkingWithEveryKindOfNotice() {
        List<String> faxes = new ArrayList<>();
        Channel fax = (customer, subject, body) -> faxes.add("FAX " + customer.name() + " | " + subject);
        CustomerNotifier notifier = new CustomerNotifier(fax);

        notifier.screeningMoved(new ScreeningMoved(DIUNA, MOVED, List.of(ANNA)));
        notifier.screeningCancelled(new ScreeningCancelled(DIUNA, "awaria projektora", List.of(CELINA)));

        assertEquals(List.of("FAX Anna | Zmiana seansu: Diuna", "FAX Celina | Odwolany seans: Diuna"), faxes);
    }

    @Test
    void schedulerKnowsOnlyTheListenerInterface() {
        Set<Class<?>> concreteListeners = Set.of(CustomerNotifier.class, Waitlist.class, ChangeStats.class);
        List<String> leaks = Arrays.stream(ScreeningScheduler.class.getDeclaredFields())
                .map(Field::getType)
                .filter(concreteListeners::contains)
                .map(Class::getSimpleName)
                .toList();
        assertEquals(List.of(), leaks, "scheduler nie trzyma konkretnych odbiorcow");
    }

    @Test
    void unsubscribedListenerIsNotNotified() {
        Journal journal = new Journal();
        ScreeningScheduler scheduler = new ScreeningScheduler(journal);
        ChangeStats stats = new ChangeStats(journal);
        scheduler.subscribe(stats);
        scheduler.unsubscribe(stats);
        scheduler.register(DIUNA);

        scheduler.move("S1", "2026-10-31 20:00", "2", List.of(ANNA));

        assertEquals(List.of("SCHEDULE Diuna 2026-10-30 18:00 -> 2026-10-31 20:00 sala 2"), journal.lines());
        assertFalse(journal.lines().stream().anyMatch(line -> line.startsWith("STATS")));
    }
}
