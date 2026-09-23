package pl.training.workshop.m8.s07_adr.start.pricing;

import pl.training.workshop.m8.s07_adr.start.notification.GroupMailer;

/**
 * Start: cennik narusza oba punkty ADR-0007 - sam wysyła powiadomienie (zależność
 * pricing -> notification) i liczy kwoty w double.
 */
public final class TicketPricing {
    private final GroupMailer mailer;

    public TicketPricing(GroupMailer mailer) {
        this.mailer = mailer;
    }

    public double total(String organizer, int tickets, double unitPrice) {
        double sum = unitPrice * tickets;
        if (tickets >= 10) {
            sum = sum - sum * 0.10;
            mailer.groupDiscountGranted(organizer, tickets);
        }
        return Math.round(sum * 100) / 100.0;
    }
}
