package pl.training.workshop.exercises.e03_notifications.step1

import pl.training.workshop.exercises.e03_notifications.Cancel
import pl.training.workshop.exercises.e03_notifications.Move
import pl.training.workshop.exercises.e03_notifications.Scenario

/**
 * Krok 1: korzen kompozycji rejestruje standardowy zestaw odbiorcow w kolejnosci, ktora byla
 * zaszyta w schedulerze: powiadomienia klientow, lista oczekujacych, statystyki.
 */
class ScreeningDesk {
    fun run(scenario: Scenario): String {
        val journal = Journal()
        val scheduler = ScreeningScheduler(journal)
        scheduler.subscribe(CustomerNotifier(journal))
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
}
