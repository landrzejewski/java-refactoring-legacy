package pl.training.workshop.exercises.e03_notifications;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Dziennik efektow zmiany repertuaru: wpis repertuaru, powiadomienie kazdego klienta wybranym
 * kanalem (w kolejnosci klientow), reakcja listy oczekujacych, statystyki. Kolejnosc wpisow,
 * tresc wiadomosci, skracanie SMS i wyjatki sa kontraktem.
 */
final class E03EquivalenceTest {
    private static final Screening DIUNA = new Screening("S1", "Diuna", "2026-10-30 18:00", "1");
    private static final Customer ANNA = new Customer("Anna", "anna@kino.pl", "600100200", "tok-anna");
    private static final Customer BARTEK = new Customer("Bartek", "bartek@kino.pl", "600300400", null);
    private static final Customer CELINA = new Customer("Celina", "celina@kino.pl", null, null);
    private static final Customer DANIEL = new Customer("Daniel", null, null, null);
    private static final Customer EWA = new Customer("Ewa", "ewa@kino.pl", "600500600", "expired-ewa");
    private static final Customer FILIP = new Customer("Filip", null, "600700800", "  ");
    private static final List<Customer> WAITING = List.of(BARTEK, CELINA);
    private static final Move MOVE = new Move("2026-10-31 20:00", "2");
    private static final Cancel CANCEL = new Cancel("awaria projektora");

    private static final String MOVED_SCHEDULE = "SCHEDULE Diuna 2026-10-30 18:00 -> 2026-10-31 20:00 sala 2";
    private static final String MOVED_BODY =
            "Seans Diuna z 2026-10-30 18:00 przeniesiony na 2026-10-31 20:00, sala 2. Bilety zachowuja waznosc.";
    private static final String MOVED_SMS = "Zmiana seansu: Diuna. Seans Diuna z 2026-10-30 18:00 przeniesiony n...";
    private static final String CANCELLED_SCHEDULE = "SCHEDULE Diuna 2026-10-30 18:00 odwolany: awaria projektora";
    private static final String CANCELLED_BODY =
            "Seans Diuna z 2026-10-30 18:00 odwolany (awaria projektora). Zwrot w ciagu 7 dni.";
    private static final String CANCELLED_SMS = "Odwolany seans: Diuna. Seans Diuna z 2026-10-30 18:00 odwolany (awa...";
    private static final String WAITLIST_KEPT = "WAITLIST Diuna: 2 oczekujacych zachowuje kolejnosc";
    private static final String WAITLIST_CLEARED = "WAITLIST Diuna: lista wyczyszczona (2)";

    @TestFactory
    Stream<DynamicTest> everyStepProducesTheSameJournal() {
        return Scene.<Scenario, String>variants()
                .variant("start", s -> new pl.training.workshop.exercises.e03_notifications.start.ScreeningDesk().run(s))
                .variant("step1", s -> new pl.training.workshop.exercises.e03_notifications.step1.ScreeningDesk().run(s))
                .variant("step2", s -> new pl.training.workshop.exercises.e03_notifications.step2.ScreeningDesk().run(s))
                .expect("przeniesienie, klient z push", scenario(List.of(ANNA), MOVE), lines(
                        MOVED_SCHEDULE,
                        "PUSH tok-anna | Zmiana seansu: Diuna | " + MOVED_BODY,
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0"))
                .expect("przeniesienie, niewazny token push -> SMS skrocony do 70 znakow", scenario(List.of(EWA), MOVE), lines(
                        MOVED_SCHEDULE,
                        "SMS 600500600 | " + MOVED_SMS,
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0"))
                .expect("przeniesienie, pusty token push liczy sie jak brak", scenario(List.of(FILIP), MOVE), lines(
                        MOVED_SCHEDULE,
                        "SMS 600700800 | " + MOVED_SMS,
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0"))
                .expect("przeniesienie, tylko e-mail", scenario(List.of(CELINA), MOVE), lines(
                        MOVED_SCHEDULE,
                        "EMAIL celina@kino.pl | Zmiana seansu: Diuna | " + MOVED_BODY,
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0"))
                .expect("przeniesienie, klient bez kanalu", scenario(List.of(DANIEL), MOVE), lines(
                        MOVED_SCHEDULE,
                        "UNDELIVERED Daniel (brak kanalu)",
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0"))
                .expect("odwolanie, trzech klientow w kolejnosci listy", scenario(List.of(BARTEK, ANNA, DANIEL), CANCEL), lines(
                        CANCELLED_SCHEDULE,
                        "SMS 600300400 | " + CANCELLED_SMS,
                        "PUSH tok-anna | Odwolany seans: Diuna | " + CANCELLED_BODY,
                        "UNDELIVERED Daniel (brak kanalu)",
                        WAITLIST_CLEARED,
                        "STATS przeniesione=0 odwolane=1"))
                .expect("przeniesienie, potem odwolanie z nowego terminu",
                        scenario(List.of(CELINA), MOVE, new Cancel("choroba obsady")), lines(
                        MOVED_SCHEDULE,
                        "EMAIL celina@kino.pl | Zmiana seansu: Diuna | " + MOVED_BODY,
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0",
                        "SCHEDULE Diuna 2026-10-31 20:00 odwolany: choroba obsady",
                        "EMAIL celina@kino.pl | Odwolany seans: Diuna | "
                                + "Seans Diuna z 2026-10-31 20:00 odwolany (choroba obsady). Zwrot w ciagu 7 dni.",
                        WAITLIST_CLEARED,
                        "STATS przeniesione=1 odwolane=1"))
                .expect("drugie przeniesienie liczy od nowego terminu",
                        scenario(List.of(ANNA), MOVE, new Move("2026-11-01 17:00", "3")), lines(
                        MOVED_SCHEDULE,
                        "PUSH tok-anna | Zmiana seansu: Diuna | " + MOVED_BODY,
                        WAITLIST_KEPT,
                        "STATS przeniesione=1 odwolane=0",
                        "SCHEDULE Diuna 2026-10-31 20:00 -> 2026-11-01 17:00 sala 3",
                        "PUSH tok-anna | Zmiana seansu: Diuna | Seans Diuna z 2026-10-31 20:00 przeniesiony na "
                                + "2026-11-01 17:00, sala 3. Bilety zachowuja waznosc.",
                        WAITLIST_KEPT,
                        "STATS przeniesione=2 odwolane=0"))
                .expect("odwolanie, potem przeniesienie", scenario(List.of(ANNA), CANCEL, MOVE), lines(
                        CANCELLED_SCHEDULE,
                        "PUSH tok-anna | Odwolany seans: Diuna | " + CANCELLED_BODY,
                        WAITLIST_CLEARED,
                        "STATS przeniesione=0 odwolane=1",
                        "ERROR: screening already cancelled: Diuna"))
                .expect("odwolanie dwa razy", scenario(List.of(), CANCEL, CANCEL), lines(
                        CANCELLED_SCHEDULE,
                        WAITLIST_CLEARED,
                        "STATS przeniesione=0 odwolane=1",
                        "ERROR: screening already cancelled: Diuna"))
                .expect("bez posiadaczy biletow i bez oczekujacych",
                        new Scenario(DIUNA, List.of(), List.of(), List.of(MOVE)), lines(
                        MOVED_SCHEDULE,
                        "WAITLIST Diuna: 0 oczekujacych zachowuje kolejnosc",
                        "STATS przeniesione=1 odwolane=0"))
                .expect("pusty nowy termin", scenario(List.of(ANNA), new Move(" ", "2")),
                        "ERROR: new start must not be blank")
                .expect("pusta sala", scenario(List.of(ANNA), new Move("2026-10-31 20:00", "")),
                        "ERROR: hall must not be blank")
                .expect("odwolanie bez powodu", scenario(List.of(ANNA), new Cancel(null)),
                        "ERROR: reason must not be blank")
                .tests();
    }

    private static Scenario scenario(List<Customer> ticketHolders, Change... changes) {
        return new Scenario(DIUNA, ticketHolders, WAITING, List.of(changes));
    }

    private static String lines(String... lines) {
        return String.join("\n", lines);
    }
}
