package pl.training.workshop.m8.s04_shadowlimits.step3;

import java.util.List;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: Separate Query from Modifier - czysta część nowej ścieżki. Nie ma dostępu do
 * żadnego portu efektów, więc w cieniu nie da się jej użyć "za mocno".
 */
public final class BookingPlanner {
    private static final Money TICKET_2D = Money.of("25.00");
    private static final Money ONLINE_FEE = Money.of("2.00");

    public BookingPlan plan(BookingRequest request) {
        Money tickets = TICKET_2D.times(request.tickets());
        Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
        return new BookingPlan("OK " + total, List.of(
                new BookingPlan.Charge(request.card(), total),
                new BookingPlan.Save(request.title() + ";" + request.email() + ";"
                        + request.tickets() + ";" + total),
                new BookingPlan.SendMail(request.email(),
                        "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets)));
    }
}
