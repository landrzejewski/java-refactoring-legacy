package pl.training.workshop.exercises.e03_notifications

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.support.Scene

/**
 * Dziennik efektow zmiany repertuaru: wpis repertuaru, powiadomienie kazdego klienta wybranym
 * kanalem (w kolejnosci klientow), reakcja listy oczekujacych, statystyki. Kolejnosc wpisow,
 * tresc wiadomosci, skracanie SMS i wyjatki sa kontraktem.
 */
class E03EquivalenceTest {
    @TestFactory
    fun everyStepProducesTheSameJournal(): List<DynamicTest> = Scene.variants<Scenario, String>()
        .variant("start") { s -> pl.training.workshop.exercises.e03_notifications.start.ScreeningDesk().run(s) }
        .variant("step1") { s -> pl.training.workshop.exercises.e03_notifications.step1.ScreeningDesk().run(s) }
        .variant("step2") { s -> pl.training.workshop.exercises.e03_notifications.step2.ScreeningDesk().run(s) }
        .expect(
            "przeniesienie, klient z push", scenario(listOf(ANNA), MOVE), lines(
                MOVED_SCHEDULE,
                "PUSH tok-anna | Zmiana seansu: Diuna | $MOVED_BODY",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0"
            )
        )
        .expect(
            "przeniesienie, niewazny token push -> SMS skrocony do 70 znakow", scenario(listOf(EWA), MOVE), lines(
                MOVED_SCHEDULE,
                "SMS 600500600 | $MOVED_SMS",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0"
            )
        )
        .expect(
            "przeniesienie, pusty token push liczy sie jak brak", scenario(listOf(FILIP), MOVE), lines(
                MOVED_SCHEDULE,
                "SMS 600700800 | $MOVED_SMS",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0"
            )
        )
        .expect(
            "przeniesienie, tylko e-mail", scenario(listOf(CELINA), MOVE), lines(
                MOVED_SCHEDULE,
                "EMAIL celina@kino.pl | Zmiana seansu: Diuna | $MOVED_BODY",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0"
            )
        )
        .expect(
            "przeniesienie, klient bez kanalu", scenario(listOf(DANIEL), MOVE), lines(
                MOVED_SCHEDULE,
                "UNDELIVERED Daniel (brak kanalu)",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0"
            )
        )
        .expect(
            "odwolanie, trzech klientow w kolejnosci listy", scenario(listOf(BARTEK, ANNA, DANIEL), CANCEL), lines(
                CANCELLED_SCHEDULE,
                "SMS 600300400 | $CANCELLED_SMS",
                "PUSH tok-anna | Odwolany seans: Diuna | $CANCELLED_BODY",
                "UNDELIVERED Daniel (brak kanalu)",
                WAITLIST_CLEARED,
                "STATS przeniesione=0 odwolane=1"
            )
        )
        .expect(
            "przeniesienie, potem odwolanie z nowego terminu",
            scenario(listOf(CELINA), MOVE, Cancel("choroba obsady")), lines(
                MOVED_SCHEDULE,
                "EMAIL celina@kino.pl | Zmiana seansu: Diuna | $MOVED_BODY",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0",
                "SCHEDULE Diuna 2026-10-31 20:00 odwolany: choroba obsady",
                "EMAIL celina@kino.pl | Odwolany seans: Diuna | " +
                    "Seans Diuna z 2026-10-31 20:00 odwolany (choroba obsady). Zwrot w ciagu 7 dni.",
                WAITLIST_CLEARED,
                "STATS przeniesione=1 odwolane=1"
            )
        )
        .expect(
            "drugie przeniesienie liczy od nowego terminu",
            scenario(listOf(ANNA), MOVE, Move("2026-11-01 17:00", "3")), lines(
                MOVED_SCHEDULE,
                "PUSH tok-anna | Zmiana seansu: Diuna | $MOVED_BODY",
                WAITLIST_KEPT,
                "STATS przeniesione=1 odwolane=0",
                "SCHEDULE Diuna 2026-10-31 20:00 -> 2026-11-01 17:00 sala 3",
                "PUSH tok-anna | Zmiana seansu: Diuna | Seans Diuna z 2026-10-31 20:00 przeniesiony na " +
                    "2026-11-01 17:00, sala 3. Bilety zachowuja waznosc.",
                WAITLIST_KEPT,
                "STATS przeniesione=2 odwolane=0"
            )
        )
        .expect(
            "odwolanie, potem przeniesienie", scenario(listOf(ANNA), CANCEL, MOVE), lines(
                CANCELLED_SCHEDULE,
                "PUSH tok-anna | Odwolany seans: Diuna | $CANCELLED_BODY",
                WAITLIST_CLEARED,
                "STATS przeniesione=0 odwolane=1",
                "ERROR: screening already cancelled: Diuna"
            )
        )
        .expect(
            "odwolanie dwa razy", scenario(listOf(), CANCEL, CANCEL), lines(
                CANCELLED_SCHEDULE,
                WAITLIST_CLEARED,
                "STATS przeniesione=0 odwolane=1",
                "ERROR: screening already cancelled: Diuna"
            )
        )
        .expect(
            "bez posiadaczy biletow i bez oczekujacych",
            Scenario(DIUNA, listOf(), listOf(), listOf(MOVE)), lines(
                MOVED_SCHEDULE,
                "WAITLIST Diuna: 0 oczekujacych zachowuje kolejnosc",
                "STATS przeniesione=1 odwolane=0"
            )
        )
        .expect("pusty nowy termin", scenario(listOf(ANNA), Move(" ", "2")), "ERROR: new start must not be blank")
        .expect("pusta sala", scenario(listOf(ANNA), Move("2026-10-31 20:00", "")), "ERROR: hall must not be blank")
        .expect("odwolanie bez powodu", scenario(listOf(ANNA), Cancel(null)), "ERROR: reason must not be blank")
        .tests()

    private companion object {
        val DIUNA = Screening("S1", "Diuna", "2026-10-30 18:00", "1")
        val ANNA = Customer("Anna", "anna@kino.pl", "600100200", "tok-anna")
        val BARTEK = Customer("Bartek", "bartek@kino.pl", "600300400", null)
        val CELINA = Customer("Celina", "celina@kino.pl", null, null)
        val DANIEL = Customer("Daniel", null, null, null)
        val EWA = Customer("Ewa", "ewa@kino.pl", "600500600", "expired-ewa")
        val FILIP = Customer("Filip", null, "600700800", "  ")
        val WAITING = listOf(BARTEK, CELINA)
        val MOVE = Move("2026-10-31 20:00", "2")
        val CANCEL = Cancel("awaria projektora")

        const val MOVED_SCHEDULE = "SCHEDULE Diuna 2026-10-30 18:00 -> 2026-10-31 20:00 sala 2"
        const val MOVED_BODY =
            "Seans Diuna z 2026-10-30 18:00 przeniesiony na 2026-10-31 20:00, sala 2. Bilety zachowuja waznosc."
        const val MOVED_SMS = "Zmiana seansu: Diuna. Seans Diuna z 2026-10-30 18:00 przeniesiony n..."
        const val CANCELLED_SCHEDULE = "SCHEDULE Diuna 2026-10-30 18:00 odwolany: awaria projektora"
        const val CANCELLED_BODY =
            "Seans Diuna z 2026-10-30 18:00 odwolany (awaria projektora). Zwrot w ciagu 7 dni."
        const val CANCELLED_SMS = "Odwolany seans: Diuna. Seans Diuna z 2026-10-30 18:00 odwolany (awa..."
        const val WAITLIST_KEPT = "WAITLIST Diuna: 2 oczekujacych zachowuje kolejnosc"
        const val WAITLIST_CLEARED = "WAITLIST Diuna: lista wyczyszczona (2)"

        fun scenario(ticketHolders: List<Customer>, vararg changes: Change) =
            Scenario(DIUNA, ticketHolders, WAITING, changes.toList())

        fun lines(vararg lines: String) = lines.joinToString("\n")
    }
}
