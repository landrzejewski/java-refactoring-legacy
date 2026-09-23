package pl.training.workshop.m8.s05_boyscout.step1;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import pl.training.workshop.m8.s05_boyscout.Ticket;
import pl.training.workshop.shared.Money;

/**
 * Krok 1 (NADUŻYCIE - antyprzykład): "skoro już tu jestem, posprzątam wszystko". Obok
 * dobrych ruchów przemycono trzy zmiany zachowania: sortowanie miejsc, e-mail małymi literami
 * i pominięcie linii "Tel" bez telefonu. Test równoważności to wykrywa - ten krok cofamy.
 */
public final class TicketPrinter {
    public String print(Ticket ticket) {
        StringBuilder text = new StringBuilder();
        text.append("Film: ").append(ticket.title()).append('\n');
        text.append("Seans: ").append(ticket.start().toLocalDate()).append(' ')
                .append(ticket.start().toLocalTime()).append('\n');
        List<String> sortedSeats = ticket.seats().stream().sorted().toList();
        text.append("Miejsca: ").append(String.join(", ", sortedSeats)).append('\n');
        text.append("Klient: ").append(ticket.email().trim().toLowerCase(Locale.ROOT)).append('\n');
        if (ticket.phone() != null) {
            text.append("Tel: ").append(ticket.phone()).append('\n');
        }
        text.append("Do zaplaty: ").append(new Money(BigDecimal.valueOf(ticket.total()))).append('\n');
        return text.toString();
    }
}
