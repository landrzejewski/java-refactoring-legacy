package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Cancel;
import pl.training.workshop.exercises.e03_notifications.Change;
import pl.training.workshop.exercises.e03_notifications.Move;
import pl.training.workshop.exercises.e03_notifications.Scenario;

/**
 * Krok 1: korzen kompozycji rejestruje standardowy zestaw odbiorcow w kolejnosci, ktora byla
 * zaszyta w schedulerze: powiadomienia klientow, lista oczekujacych, statystyki.
 */
public final class ScreeningDesk {
    public String run(Scenario scenario) {
        Journal journal = new Journal();
        ScreeningScheduler scheduler = new ScreeningScheduler(journal);
        scheduler.subscribe(new CustomerNotifier(journal));
        scheduler.subscribe(new Waitlist(journal, scenario.waiting()));
        scheduler.subscribe(new ChangeStats(journal));
        scheduler.register(scenario.screening());
        String id = scenario.screening().id();
        try {
            for (Change change : scenario.changes()) {
                switch (change) {
                    case Move move -> scheduler.move(id, move.newStart(), move.newHall(), scenario.ticketHolders());
                    case Cancel cancel -> scheduler.cancel(id, cancel.reason(), scenario.ticketHolders());
                }
            }
        } catch (IllegalArgumentException exception) {
            journal.note("ERROR: " + exception.getMessage());
        }
        return String.join("\n", journal.lines());
    }
}
