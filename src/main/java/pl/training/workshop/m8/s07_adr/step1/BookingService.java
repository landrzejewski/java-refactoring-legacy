package pl.training.workshop.m8.s07_adr.step1;

import java.util.List;
import java.util.Locale;

import pl.training.workshop.m8.s07_adr.step1.notification.GroupMailer;
import pl.training.workshop.m8.s07_adr.step1.pricing.Quote;
import pl.training.workshop.m8.s07_adr.step1.pricing.TicketPricing;

/** Krok 1: warstwa aplikacji przejmuje wysłanie powiadomienia na podstawie Quote. */
public final class BookingService {
    private final GroupMailer mailer = new GroupMailer();
    private final TicketPricing pricing = new TicketPricing();

    public String book(String organizer, int tickets, String format) {
        double unitPrice = switch (format) {
            case "IMAX" -> 40.00;
            case "3D" -> 32.00;
            default -> 25.00;
        };
        Quote quote = pricing.total(tickets, unitPrice);
        if (quote.groupDiscount()) {
            mailer.groupDiscountGranted(organizer, tickets);
        }
        return "DO ZAPLATY " + String.format(Locale.ROOT, "%.2f", quote.total());
    }

    public List<String> sentMails() {
        return mailer.sent();
    }
}
