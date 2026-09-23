package pl.training.workshop.m8.s07_adr.step2;

import java.util.List;

import pl.training.workshop.m8.s07_adr.step2.notification.GroupMailer;
import pl.training.workshop.m8.s07_adr.step2.pricing.Quote;
import pl.training.workshop.m8.s07_adr.step2.pricing.TicketPricing;
import pl.training.workshop.shared.Money;

/** Krok 2: warstwa aplikacji podaje cenę jednostkową jako Money. */
public final class BookingService {
    private final GroupMailer mailer = new GroupMailer();
    private final TicketPricing pricing = new TicketPricing();

    public String book(String organizer, int tickets, String format) {
        Money unitPrice = switch (format) {
            case "IMAX" -> Money.of("40.00");
            case "3D" -> Money.of("32.00");
            default -> Money.of("25.00");
        };
        Quote quote = pricing.total(tickets, unitPrice);
        if (quote.groupDiscount()) {
            mailer.groupDiscountGranted(organizer, tickets);
        }
        return "DO ZAPLATY " + quote.total();
    }

    public List<String> sentMails() {
        return mailer.sent();
    }
}
