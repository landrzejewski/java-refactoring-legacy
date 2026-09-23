package pl.training.workshop.m8.s04_shadowlimits.start;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.m8.s04_shadowlimits.Infrastructure;
import pl.training.workshop.shared.Money;

/**
 * Start: nowa ścieżka rezerwacji. Liczy poprawnie, ale obliczenie jest splecione z efektami:
 * sama obciąża kartę, zapisuje wiersz i wysyła mail.
 */
public final class NewBookingFlow {
    private static final Money TICKET_2D = Money.of("25.00");
    private static final Money ONLINE_FEE = Money.of("2.00");

    private final Infrastructure infra;

    public NewBookingFlow(Infrastructure infra) {
        this.infra = infra;
    }

    public String book(BookingRequest request) {
        Money tickets = TICKET_2D.times(request.tickets());
        Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
        infra.charge(request.card(), total);
        infra.save(request.title() + ";" + request.email() + ";" + request.tickets() + ";" + total);
        infra.sendMail(request.email(),
                "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets);
        return "OK " + total;
    }
}
