package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Cancel
import pl.training.workshop.exercises.e03_notifications.Move
import pl.training.workshop.exercises.e03_notifications.Scenario

/**
 * Krok 2: korzen kompozycji sklada obie struktury w jednym miejscu: kolejnosc odbiorcow zdarzen
 * (powiadomienia, lista, statystyki) i kolejnosc kanalow dostarczania (push, SMS, e-mail, niedostarczone).
 * Inna polityka kanalow (np. tylko e-mail dla festiwalu) to inny lancuch, bez zmian w pozostalych klasach.
 */
class ScreeningDesk {
    fun run(scenario: Scenario): String {
        val journal = Journal()
        val scheduler = ScreeningScheduler(journal)
        scheduler.subscribe(CustomerNotifier(standardDelivery(journal)))
        scheduler.subscribe(Waitlist(journal, scenario.waiting))
        scheduler.subscribe(ChangeStats(journal))
        scheduler.register(scenario.screening)
        val id = scenario.screening.id
        try {
            for (change in scenario.changes) {
                when (change) {
                    is Move -> scheduler.move(id, change.newStart, change.newHall, scenario.ticketHolders)
                    is Cancel -> scheduler.cancel(id, change.reason, scenario.ticketHolders)
                }
            }
        } catch (exception: IllegalArgumentException) {
            journal.note("ERROR: ${exception.message}")
        }
        return journal.lines().joinToString("\n")
    }

    companion object {
        /** Standardowa sciezka dostarczania kina: push, potem SMS, potem e-mail, na koncu wpis o niedostarczeniu. */
        internal fun standardDelivery(journal: Journal): Channel =
            PushLink(
                PushChannel(journal),
                SmsLink(
                    SmsChannel(journal),
                    EmailLink(EmailChannel(journal), UndeliveredLink(journal))
                )
            )
    }
}
