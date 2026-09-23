package pl.training.workshop.m4.s00_characterization.start;

import java.time.LocalDateTime;

import pl.training.workshop.m4.s00_characterization.Booking;

/**
 * Start: nieprzetestowany generator potwierdzenia rezerwacji z systemu kasowego.
 * Nikt nie pamięta wszystkich reguł, a dokument czytają klienci i infolinia.
 * Zanim cokolwiek zmienimy, zapisujemy test charakterystyki: co kod ROBI, a nie co POWINIEN.
 * Przeszkody: bieżący czas w dokumencie i formatowanie zależne od domyślnego Locale.
 */
public final class BookingConfirmation {
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
                + "Wygenerowano: " + LocalDateTime.now().withNano(0) + "\n";
    }
}
