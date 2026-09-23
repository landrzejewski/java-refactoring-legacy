package pl.training.workshop.m8.s07_adr.start;

import java.util.List;
import java.util.Locale;

import pl.training.workshop.m8.s07_adr.start.notification.GroupMailer;
import pl.training.workshop.m8.s07_adr.start.pricing.TicketPricing;

/** Start: warstwa aplikacji - składa cennik z powiadomieniami i formatuje odpowiedź. */
public final class BookingService {
    private final GroupMailer mailer = new GroupMailer();
    private final TicketPricing pricing = new TicketPricing(mailer);

    public String book(String organizer, int tickets, String format) {
        double unitPrice = switch (format) {
            case "IMAX" -> 40.00;
            case "3D" -> 32.00;
            default -> 25.00;
        };
        double total = pricing.total(organizer, tickets, unitPrice);
        return "DO ZAPLATY " + String.format(Locale.ROOT, "%.2f", total);
    }

    public List<String> sentMails() {
        return mailer.sent();
    }
}
