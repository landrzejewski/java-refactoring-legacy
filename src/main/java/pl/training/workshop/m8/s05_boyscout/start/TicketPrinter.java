package pl.training.workshop.m8.s05_boyscout.start;

import java.util.Locale;

import pl.training.workshop.m8.s05_boyscout.Ticket;

/**
 * Start: wydruk biletu, do którego i tak musimy zajrzeć (w tym sprincie dochodzi linia "Sala").
 * Nazwy s, d, x, ręczne sklejanie listy miejsc, konkatenacja w pętli. Kusi, żeby "posprzątać wszystko".
 */
public final class TicketPrinter {
    public String print(Ticket t) {
        String s = "";
        s = s + "Film: " + t.title() + "\n";
        String d = t.start().toLocalDate() + " " + t.start().toLocalTime();
        s = s + "Seans: " + d + "\n";
        String x = "";
        for (int i = 0; i < t.seats().size(); i++) {
            if (i > 0) {
                x = x + ", ";
            }
            x = x + t.seats().get(i);
        }
        s = s + "Miejsca: " + x + "\n";
        s = s + "Klient: " + t.email().trim() + "\n";
        if (t.phone() != null) {
            s = s + "Tel: " + t.phone() + "\n";
        } else {
            s = s + "Tel: -\n";
        }
        s = s + "Do zaplaty: " + String.format(Locale.ROOT, "%.2f", t.total()) + "\n";
        return s;
    }
}
