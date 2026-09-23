package pl.training.workshop.m4.s00_characterization.step1;

import java.time.Clock;
import java.time.LocalDateTime;

import pl.training.workshop.m4.s00_characterization.Booking;

/**
 * Krok 1: Parameterize Constructor - wstrzykujemy {@link Clock}, żeby test charakterystyki
 * mógł porównać CAŁY dokument. Bezargumentowy konstruktor zostaje z zegarem systemowym,
 * więc dotychczasowi klienci nie widzą różnicy. Reszta kodu - bajt w bajt jak w start.
 */
public final class BookingConfirmation {
    private final Clock clock;

    public BookingConfirmation() {
        this(Clock.systemDefaultZone());
    }

    public BookingConfirmation(Clock clock) {
        this.clock = clock;
    }

    public String confirm(Booking b) {
        double sum = 0;
        for (String t : b.ticketTypes()) {
            double p;
            if (b.format() == 3) {
                p = 40.00;
            } else if (b.format() == 2) {
                p = 32.00;
            } else {
                p = 25.00;
            }
            if (t.equals("S")) {
                p = p * 0.75;
            } else if (t.equals("E")) {
                p = p * 0.70;
            } else if (t.equals("C")) {
                p = p * 0.60;
            }
            if (b.start().getHour() < 12) {
                p = p - 5.00;
            }
            sum = sum + p;
        }
        if (b.ticketTypes().size() > 10) {
            sum = sum * 0.9;
        }
        double fee = b.online() ? 2.00 * b.ticketTypes().size() : 0;
        String f = b.format() == 3 ? "IMAX" : b.format() == 2 ? "3D" : "2D";
        return "POTWIERDZENIE REZERWACJI\n"
                + "Klient: " + b.customer().trim().toUpperCase() + "\n"
                + "Film: " + b.title() + ", " + f + ", " + b.start() + "\n"
                + "Bilety: " + b.ticketTypes().size() + " " + b.ticketTypes() + "\n"
                + "Bilety razem: " + String.format("%.2f", sum) + "\n"
                + "Oplata rezerwacyjna: " + String.format("%.2f", fee) + "\n"
                + "Do zaplaty: " + String.format("%.2f", sum + fee) + "\n"
                + "Wygenerowano: " + LocalDateTime.now(clock).withNano(0) + "\n";
    }
}
