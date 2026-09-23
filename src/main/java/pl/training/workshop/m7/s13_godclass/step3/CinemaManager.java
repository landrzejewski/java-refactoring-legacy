package pl.training.workshop.m7.s13_godclass.step3;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Krok 3: BookingRepository i typ Booking zamiast Object[] - trzeci pionowy wycinek.
 * Magiczne indeksy (b[7], b[10]...) zamienione na nazwane pola; dostęp do rezerwacji
 * ma jednego właściciela. Magazyn nadal jest globalny (LegacyDb), więc współdzielenie
 * stanu między instancjami CinemaManager się nie zmienia - to świadomie osobna decyzja.
 * Krok 1: PricingService, krok 2: NotificationService.
 */
public class CinemaManager {
    // format: 1 = 2D, 2 = 3D, 3 = IMAX
    // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
    // typ biletu: N = normalny, S = student, E = senior, C = dziecko

    /** Hak dla testów dodany "na chwilę" w 2019 roku. */
    static Supplier<LocalDateTime> clock = LocalDateTime::now;

    private final PricingService pricing = new PricingService();
    private final NotificationService notifications = new NotificationService();
    private final BookingRepository bookings = new BookingRepository();

    public void addScreening(String id, String title, int format,
            LocalDateTime start, int rows, int seatsPerRow, int vipFromRow) {
        LegacyDb.SCREENINGS.put(id, new Object[] {title, format, start, rows,
                seatsPerRow, vipFromRow, new HashSet<String>()});
    }

    public String book(String screeningId, String email, String phone,
            String[] seats, String[] types, boolean web, boolean ownGlasses) {
        Object[] s = LegacyDb.SCREENINGS.get(screeningId);
        if (s != null) {
            if (seats != null && seats.length > 0) {
                if (types != null && types.length == seats.length) {
                    @SuppressWarnings("unchecked")
                    Set<String> taken = (Set<String>) s[6];
                    for (String seat : seats) {
                        if (taken.contains(seat)) {
                            return "ERROR: seat taken " + seat;
                        }
                        int row = Integer.parseInt(seat.substring(1));
                        char letter = seat.charAt(0);
                        if (row > (Integer) s[3] || letter - 'A' >= (Integer) s[4]) {
                            return "ERROR: no such seat " + seat;
                        }
                    }
                    double sum = pricing.ticketsSum((Integer) s[1], (LocalDateTime) s[2], (Integer) s[5],
                            seats, types, ownGlasses);
                    double total = sum + pricing.bookingFee(web, seats.length);
                    String id = bookings.nextId();
                    for (String seat : seats) {
                        taken.add(seat);
                    }
                    bookings.save(new Booking(id, screeningId, email, phone, seats, types, web,
                            total, clock.get(), sum));
                    notifications.bookingCreated(email, id, (String) s[0], seats, total);
                    return id;
                } else {
                    return "ERROR: types do not match seats";
                }
            } else {
                return "ERROR: no seats";
            }
        } else {
            return "ERROR: no screening " + screeningId;
        }
    }

    public String pay(String bookingId, String card) {
        Booking b = bookings.find(bookingId);
        if (b == null) {
            return "ERROR: no booking";
        }
        int status = b.status();
        if (status == 1) {
            return "ERROR: already paid";
        } else if (status == 2) {
            return "ERROR: already used";
        } else if (status == 3) {
            return "ERROR: expired";
        } else if (status == 4) {
            return "ERROR: cancelled";
        }
        if (!LegacyPaymentGateway.charge(card, b.total())) {
            notifications.paymentDeclined(b.email(), bookingId);
            return "ERROR: payment declined";
        }
        b.markPaid(card);
        String email = b.email();
        int points = (int) (b.ticketsSum() / 10);
        LegacyDb.LOYALTY.put(email, LegacyDb.LOYALTY.getOrDefault(email, 0) + points);
        notifications.ticketsPaid(email, b.phone(), bookingId, b.total(), points);
        return "OK";
    }

    public String cancel(String bookingId) {
        Booking b = bookings.find(bookingId);
        if (b == null) {
            return "ERROR: no booking";
        }
        int status = b.status();
        if (status == 2 || status == 3 || status == 4) {
            return "ERROR: cannot cancel";
        }
        Object[] s = LegacyDb.SCREENINGS.get(b.screeningId());
        @SuppressWarnings("unchecked")
        Set<String> taken = (Set<String>) s[6];
        for (String seat : b.seats()) {
            taken.remove(seat);
        }
        b.status(4);
        double refund = 0;
        if (status == 1) {
            LocalDateTime now = clock.get();
            LocalDateTime start = (LocalDateTime) s[2];
            double tickets = b.ticketsSum();
            if (!now.isBefore(start)) {
                refund = 0;
            } else if (Duration.between(now, start).toHours() >= 24) {
                refund = tickets;
            } else {
                refund = tickets * 0.5;
            }
            refund = refund - 3.00;
            if (refund < 0) {
                refund = 0;
            }
            refund = Math.round(refund * 100) / 100.0;
            if (refund > 0) {
                LegacyPaymentGateway.refund(b.card(), refund);
            }
            String email = b.email();
            int points = (int) (tickets / 10);
            LegacyDb.LOYALTY.put(email, Math.max(0, LegacyDb.LOYALTY.getOrDefault(email, 0) - points));
        }
        notifications.bookingCancelled(b.email(), bookingId, refund);
        return "REFUND " + fmt(refund);
    }

    public void expireOld() {
        LocalDateTime now = clock.get();
        for (Booking b : bookings.all()) {
            if (b.status() == 0
                    && Duration.between(b.createdAt(), now).toMinutes() >= 15) {
                b.status(3);
                Object[] s = LegacyDb.SCREENINGS.get(b.screeningId());
                @SuppressWarnings("unchecked")
                Set<String> taken = (Set<String>) s[6];
                for (String seat : b.seats()) {
                    taken.remove(seat);
                }
                notifications.bookingExpired(b.email(), b.id());
            }
        }
    }

    public String use(String bookingId) {
        Booking b = bookings.find(bookingId);
        if (b == null) {
            return "ERROR: no booking";
        }
        if (b.status() != 1) {
            return "ERROR: not paid";
        }
        b.status(2);
        return "OK";
    }

    public int loyaltyPoints(String email) {
        return LegacyDb.LOYALTY.getOrDefault(email, 0);
    }

    public String dailyReport(LocalDate day) {
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
                    .append(" bil., ").append(fmt(e.getValue()[1])).append("\n");
            revenue = revenue + e.getValue()[1];
        }
        sb.append("Biletow: ").append(tickets).append("\n");
        sb.append("Przychod z biletow: ").append(fmt(revenue)).append("\n");
        sb.append("Oplaty rezerwacyjne: ").append(fmt(fees)).append("\n");
        sb.append("Netto (bez VAT 8%): ").append(fmt(revenue / 1.08)).append("\n");
        return sb.toString();
    }

    public String settlement(String title, int week) {
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
                + fmt(revenue) + ", dla dystrybutora " + fmt(share);
    }

    public List<String> freeSeats(String screeningId) {
        Object[] s = LegacyDb.SCREENINGS.get(screeningId);
        List<String> free = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Set<String> taken = (Set<String>) s[6];
        for (int r = 1; r <= (Integer) s[3]; r++) {
            for (int c = 0; c < (Integer) s[4]; c++) {
                String seat = "" + (char) ('A' + c) + r;
                if (!taken.contains(seat)) {
                    free.add(seat);
                }
            }
        }
        return free;
    }

    private static String fmt(double value) {
        return Formats.amount(value);
    }
}
