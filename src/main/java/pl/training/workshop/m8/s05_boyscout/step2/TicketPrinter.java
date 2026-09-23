package pl.training.workshop.m8.s05_boyscout.step2;

import java.util.Locale;

import pl.training.workshop.m8.s05_boyscout.Ticket;

/**
 * Krok 2: poprawna, mała poprawa Boy Scout - tylko w dotykanej metodzie i bez zmiany kontraktu:
 * Rename (⇧F6), StringBuilder zamiast konkatenacji, String.join zamiast ręcznej pętli,
 * Extract Method dla linii telefonu. Sortowanie, wielkość liter i format zostają - to decyzje biznesowe.
 */
public final class TicketPrinter {
    public String print(Ticket ticket) {
        return new StringBuilder()
                .append("Film: ").append(ticket.title()).append('\n')
                .append("Seans: ").append(ticket.start().toLocalDate()).append(' ')
                .append(ticket.start().toLocalTime()).append('\n')
                .append("Miejsca: ").append(String.join(", ", ticket.seats())).append('\n')
                .append("Klient: ").append(ticket.email().trim()).append('\n')
                .append(phoneLine(ticket)).append('\n')
                .append("Do zaplaty: ").append(String.format(Locale.ROOT, "%.2f", ticket.total()))
                .append('\n')
                .toString();
    }

    private static String phoneLine(Ticket ticket) {
        return "Tel: " + (ticket.phone() != null ? ticket.phone() : "-");
    }
}
