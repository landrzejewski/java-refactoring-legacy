package pl.training.workshop.exercises.e03_notifications

import java.util.concurrent.atomic.AtomicInteger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

import pl.training.workshop.exercises.e03_notifications.step2.ChangeStats
import pl.training.workshop.exercises.e03_notifications.step2.Channel
import pl.training.workshop.exercises.e03_notifications.step2.CustomerNotifier
import pl.training.workshop.exercises.e03_notifications.step2.EmailChannel
import pl.training.workshop.exercises.e03_notifications.step2.EmailLink
import pl.training.workshop.exercises.e03_notifications.step2.Journal
import pl.training.workshop.exercises.e03_notifications.step2.PushChannel
import pl.training.workshop.exercises.e03_notifications.step2.PushLink
import pl.training.workshop.exercises.e03_notifications.step2.ScreeningCancelled
import pl.training.workshop.exercises.e03_notifications.step2.ScreeningMoved
import pl.training.workshop.exercises.e03_notifications.step2.ScreeningScheduler
import pl.training.workshop.exercises.e03_notifications.step2.SmsLink
import pl.training.workshop.exercises.e03_notifications.step2.UndeliveredLink
import pl.training.workshop.exercises.e03_notifications.step2.Waitlist

/**
 * Kontrakt rozwiazania: nadawca zna tylko interfejs odbiorcow, nowy kanal dziala z kazdym rodzajem wiadomosci,
 * a ogniwo dostarczania przekazuje dalej tylko to, czego samo nie obsluzylo.
 */
class E03SolutionTest {
    @Test
    fun linkPassesOnOnlyWhatItCannotDeliver() {
        val journal = Journal()
        val passedOn = AtomicInteger()
        val spy = Channel { _, _, _ -> passedOn.incrementAndGet() }
        val push = PushLink(PushChannel(journal), spy)

        push.deliver(ANNA, "tytul", "tresc")
        assertEquals(listOf("PUSH tok-anna | tytul | tresc"), journal.lines())
        assertEquals(0, passedOn.get(), "obsluzone zadanie nie idzie dalej")

        push.deliver(CELINA, "tytul", "tresc")
        assertEquals(1, passedOn.get(), "nieobsluzone zadanie idzie dokladnie do nastepnika")
        assertEquals(1, journal.lines().size, "ogniwo bez kanalu nic nie wysyla")
    }

    @Test
    fun chainIsAssembledExplicitlySoAFestivalCanUseEmailOnly() {
        val journal = Journal()
        val emailOnly: Channel = EmailLink(EmailChannel(journal), UndeliveredLink(journal))
        CustomerNotifier(emailOnly).screeningMoved(ScreeningMoved(DIUNA, MOVED, listOf(ANNA)))
        assertEquals(
            listOf(
                "EMAIL anna@kino.pl | Zmiana seansu: Diuna | Seans Diuna z 2026-10-30 18:00 " +
                    "przeniesiony na 2026-10-31 20:00, sala 2. Bilety zachowuja waznosc."
            ),
            journal.lines()
        )
    }

    @Test
    fun everyLinkKnowsOnlyItsSuccessor() {
        for (link in listOf(PushLink::class.java, SmsLink::class.java, EmailLink::class.java)) {
            val successors = link.declaredFields.map { it.type }.count { it == Channel::class.java }
            assertEquals(1, successors, link.simpleName)
        }
        val endSuccessors = UndeliveredLink::class.java.declaredFields.map { it.type }.count { it == Channel::class.java }
        assertEquals(0, endSuccessors, "koniec lancucha nie ma nastepnika")
    }

    @Test
    fun newChannelIsOneClassWorkingWithEveryKindOfNotice() {
        val faxes = mutableListOf<String>()
        val fax = Channel { customer, subject, _ -> faxes += "FAX ${customer.name} | $subject" }

        val notifier = CustomerNotifier(fax)

        notifier.screeningMoved(ScreeningMoved(DIUNA, MOVED, listOf(ANNA)))
        notifier.screeningCancelled(ScreeningCancelled(DIUNA, "awaria projektora", listOf(CELINA)))

        assertEquals(listOf("FAX Anna | Zmiana seansu: Diuna", "FAX Celina | Odwolany seans: Diuna"), faxes)
    }

    @Test
    fun schedulerKnowsOnlyTheListenerInterface() {
        val concreteListeners = setOf(CustomerNotifier::class.java, Waitlist::class.java, ChangeStats::class.java)
        val leaks = ScreeningScheduler::class.java.declaredFields
            .map { it.type }
            .filter { it in concreteListeners }
            .map { it.simpleName }
        assertEquals(listOf<String>(), leaks, "scheduler nie trzyma konkretnych odbiorcow")
    }

    @Test
    fun unsubscribedListenerIsNotNotified() {
        val journal = Journal()
        val scheduler = ScreeningScheduler(journal)
        val stats = ChangeStats(journal)
        scheduler.subscribe(stats)
        scheduler.unsubscribe(stats)
        scheduler.register(DIUNA)

        scheduler.move("S1", "2026-10-31 20:00", "2", listOf(ANNA))

        assertEquals(listOf("SCHEDULE Diuna 2026-10-30 18:00 -> 2026-10-31 20:00 sala 2"), journal.lines())
        assertFalse(journal.lines().any { it.startsWith("STATS") })
    }

    private companion object {
        val DIUNA = Screening("S1", "Diuna", "2026-10-30 18:00", "1")
        val MOVED = Screening("S1", "Diuna", "2026-10-31 20:00", "2")
        val ANNA = Customer("Anna", "anna@kino.pl", "600100200", "tok-anna")
        val CELINA = Customer("Celina", "celina@kino.pl", null, null)
    }
}
