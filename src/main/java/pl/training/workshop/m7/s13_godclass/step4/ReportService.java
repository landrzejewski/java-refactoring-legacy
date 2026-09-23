package pl.training.workshop.m7.s13_godclass.step4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Krok 4: raporty mają jednego właściciela. Czytają rezerwacje przez BookingRepository
 * (nie przez Object[]), więc kolejny krok może zmienić magazyn bez dotykania raportów.
 * Kod metod przeniesiony dosłownie - kolejność sumowania double bez zmian.
 */
final class ReportService {
    private final BookingRepository bookings;

    ReportService(BookingRepository bookings) {
        this.bookings = Objects.requireNonNull(bookings, "bookings");
    }

    String dailyReport(LocalDate day) {
        StringBuilder sb = new StringBuilder();
        sb.append("RAPORT DZIENNY ").append(day).append("\n");
        Map<String, double[]> byTitle = new TreeMap<>();
        double fees = 0;
        int tickets = 0;
        for (Booking b : bookings.all()) {
            int status = b.status();
            if (status != 1 && status != 2) {
                continue;
            }
            Object[] s = LegacyDb.SCREENINGS.get(b.screeningId());
            if (!((LocalDateTime) s[2]).toLocalDate().equals(day)) {
                continue;
            }
            double[] row = byTitle.computeIfAbsent((String) s[0], k -> new double[2]);
            row[0] = row[0] + b.seats().length;
            row[1] = row[1] + b.ticketsSum();
            fees = fees + (b.total() - b.ticketsSum());
            tickets = tickets + b.seats().length;
        }
        double revenue = 0;
        for (Map.Entry<String, double[]> e : byTitle.entrySet()) {
            sb.append(e.getKey()).append(": ").append((int) e.getValue()[0])
                    .append(" bil., ").append(Formats.amount(e.getValue()[1])).append("\n");
            revenue = revenue + e.getValue()[1];
        }
        sb.append("Biletow: ").append(tickets).append("\n");
        sb.append("Przychod z biletow: ").append(Formats.amount(revenue)).append("\n");
        sb.append("Oplaty rezerwacyjne: ").append(Formats.amount(fees)).append("\n");
        sb.append("Netto (bez VAT 8%): ").append(Formats.amount(revenue / 1.08)).append("\n");
        return sb.toString();
    }

    String settlement(String title, int week) {
        double revenue = 0;
        for (Booking b : bookings.all()) {
            int status = b.status();
            Object[] s = LegacyDb.SCREENINGS.get(b.screeningId());
            if ((status == 1 || status == 2) && s[0].equals(title)) {
                revenue = revenue + b.ticketsSum();
            }
        }
        double share;
        if (week == 1) {
            share = revenue * 0.50;
        } else if (week == 2) {
            share = revenue * 0.40;
        } else {
            share = revenue * 0.35;
        }
        if (share < 500.00) {
            share = 500.00;
        }
        return "ROZLICZENIE " + title + " tydzien " + week + ": przychod "
                + Formats.amount(revenue) + ", dla dystrybutora " + Formats.amount(share);
    }
}
