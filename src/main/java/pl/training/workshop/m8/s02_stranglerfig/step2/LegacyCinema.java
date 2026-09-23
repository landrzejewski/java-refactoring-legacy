package pl.training.workshop.m8.s02_stranglerfig.step2;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.m8.s02_stranglerfig.BookingLedger;
import pl.training.workshop.m8.s02_stranglerfig.CinemaApi;
import pl.training.workshop.shared.Money;

/**
 * Krok 2 (bez zmian): stary system. Od kroku 1 klienci widzą go tylko przez CinemaFacade.
 */
public final class LegacyCinema implements CinemaApi {
    private final BookingLedger ledger;

    public LegacyCinema(BookingLedger ledger) {
        this.ledger = ledger;
    }

    @Override
    public String book(String email, String title, int format, int tickets, boolean web) {
        if (tickets <= 0) {
            return "ERROR: no seats";
        }
        double p = format == 1 ? 25.00 : format == 2 ? 32.00 : 40.00;
        double sum = p * tickets;
        if (tickets >= 10) {
            sum = sum - sum * 0.10;
        }
        double fees = web ? 2.00 * tickets : 0;
        String id = ledger.nextId();
        ledger.add(new BookingLedger.Booking(id, email, title, tickets,
                new Money(BigDecimal.valueOf(sum)), new Money(BigDecimal.valueOf(fees))));
        return id;
    }

    @Override
    public String report() {
        Map<String, double[]> byTitle = new TreeMap<>();
        double fees = 0;
        int count = 0;
        for (BookingLedger.Booking b : ledger.all()) {
            double[] row = byTitle.computeIfAbsent(b.title(), k -> new double[2]);
            row[0] = row[0] + b.tickets();
            row[1] = row[1] + b.ticketsValue().amount().doubleValue();
            fees = fees + b.fees().amount().doubleValue();
            count = count + b.tickets();
        }
        StringBuilder sb = new StringBuilder("RAPORT\n");
        double revenue = 0;
        for (Map.Entry<String, double[]> e : byTitle.entrySet()) {
            sb.append(e.getKey()).append(": ").append((int) e.getValue()[0]).append(" bil., ")
                    .append(String.format(Locale.ROOT, "%.2f", e.getValue()[1])).append("\n");
            revenue = revenue + e.getValue()[1];
        }
        sb.append("Biletow: ").append(count).append("\n");
        sb.append("Przychod z biletow: ").append(String.format(Locale.ROOT, "%.2f", revenue)).append("\n");
        sb.append("Oplaty rezerwacyjne: ").append(String.format(Locale.ROOT, "%.2f", fees)).append("\n");
        return sb.toString();
    }
}
