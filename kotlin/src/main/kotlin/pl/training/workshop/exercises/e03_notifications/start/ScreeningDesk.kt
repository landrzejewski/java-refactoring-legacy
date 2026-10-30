package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Cancel
import pl.training.workshop.exercises.e03_notifications.Move
import pl.training.workshop.exercises.e03_notifications.Scenario

/** Start: klient sceny - kierownik kina wykonuje kolejne zmiany repertuaru i oddaje dziennik efektow. */
class ScreeningDesk {
    fun run(scenario: Scenario): String {
        val journal = Journal()
        val scheduler = ScreeningScheduler(journal, Waitlist(journal, scenario.waiting), ChangeStats(journal))
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
}
