package pl.training.workshop.exercises.e03_notifications.step2;

import pl.training.workshop.exercises.e03_notifications.Cancel;
import pl.training.workshop.exercises.e03_notifications.Change;
import pl.training.workshop.exercises.e03_notifications.Move;
import pl.training.workshop.exercises.e03_notifications.Scenario;

/**
 * Krok 2: korzen kompozycji sklada obie struktury w jednym miejscu: kolejnosc odbiorcow zdarzen
 * (powiadomienia, lista, statystyki) i kolejnosc kanalow dostarczania (push, SMS, e-mail, niedostarczone).
 * Inna polityka kanalow (np. tylko e-mail dla festiwalu) to inny lancuch, bez zmian w pozostalych klasach.
 */
public final class ScreeningDesk {
    public String run(Scenario scenario) {
        Journal journal = new Journal();
        ScreeningScheduler scheduler = new ScreeningScheduler(journal);
        scheduler.subscribe(new CustomerNotifier(standardDelivery(journal)));
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

    /** Standardowa sciezka dostarczania kina: push, potem SMS, potem e-mail, na koncu wpis o niedostarczeniu. */
    static Channel standardDelivery(Journal journal) {
        return new PushLink(new PushChannel(journal),
                new SmsLink(new SmsChannel(journal),
                        new EmailLink(new EmailChannel(journal),
                                new UndeliveredLink(journal))));
    }
}
