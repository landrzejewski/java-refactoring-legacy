package pl.training.workshop.m7.s13_godclass.step2;

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
 * Krok 2: Extract Class NotificationService - drugi pionowy wycinek.
 * Wszystkie maile i SMS-y idą przez jednego właściciela treści powiadomień;
 * CinemaManager decyduje KIEDY powiadomić, NotificationService - CO i JAK.
 * Kolejność efektów (mail przed SMS, powiadomienie po zapisie) bez zmian.
 * Krok 1: PricingService.
 */
public class CinemaManager {
    // format: 1 = 2D, 2 = 3D, 3 = IMAX
    // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
    // typ biletu: N = normalny, S = student, E = senior, C = dziecko

    /** Hak dla testów dodany "na chwilę" w 2019 roku. */
    static Supplier<LocalDateTime> clock = LocalDateTime::now;

    private final PricingService pricing = new PricingService();
    private final NotificationService notifications = new NotificationService();

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
                    String id = "B" + (LegacyDb.sequence++);
                    for (String seat : seats) {
                        taken.add(seat);
                    }
                    LegacyDb.BOOKINGS.put(id, new Object[] {screeningId, email, phone,
                            seats, types, web, total, 0, clock.get(), null, sum});
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
        Object[] b = LegacyDb.BOOKINGS.get(bookingId);
        if (b == null) {
            return "ERROR: no booking";
        }
        int status = (Integer) b[7];
        if (status == 1) {
            return "ERROR: already paid";
        } else if (status == 2) {
            return "ERROR: already used";
        } else if (status == 3) {
            return "ERROR: expired";
        } else if (status == 4) {
            return "ERROR: cancelled";
        }
        if (!LegacyPaymentGateway.charge(card, (Double) b[6])) {
            notifications.paymentDeclined((String) b[1], bookingId);
            return "ERROR: payment declined";
        }
        b[7] = 1;
        b[9] = card;
        String email = (String) b[1];
        int points = (int) (((Double) b[10]) / 10);
        LegacyDb.LOYALTY.put(email, LegacyDb.LOYALTY.getOrDefault(email, 0) + points);
        notifications.ticketsPaid(email, (String) b[2], bookingId, (Double) b[6], points);
        return "OK";
    }

    public String cancel(String bookingId) {
        Object[] b = LegacyDb.BOOKINGS.get(bookingId);
        if (b == null) {
            return "ERROR: no booking";
        }
        int status = (Integer) b[7];
        if (status == 2 || status == 3 || status == 4) {
            return "ERROR: cannot cancel";
        }
        Object[] s = LegacyDb.SCREENINGS.get((String) b[0]);
        @SuppressWarnings("unchecked")
        Set<String> taken = (Set<String>) s[6];
        for (String seat : (String[]) b[3]) {
            taken.remove(seat);
        }
        b[7] = 4;
        double refund = 0;
        if (status == 1) {
            LocalDateTime now = clock.get();
            LocalDateTime start = (LocalDateTime) s[2];
            double tickets = (Double) b[10];
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
                LegacyPaymentGateway.refund((String) b[9], refund);
            }
            String email = (String) b[1];
            int points = (int) (tickets / 10);
            LegacyDb.LOYALTY.put(email, Math.max(0, LegacyDb.LOYALTY.getOrDefault(email, 0) - points));
        }
        notifications.bookingCancelled((String) b[1], bookingId, refund);
        return "REFUND " + fmt(refund);
    }

    public void expireOld() {
        LocalDateTime now = clock.get();
        for (Map.Entry<String, Object[]> e : LegacyDb.BOOKINGS.entrySet()) {
            Object[] b = e.getValue();
            if ((Integer) b[7] == 0
                    && Duration.between((LocalDateTime) b[8], now).toMinutes() >= 15) {
                b[7] = 3;
                Object[] s = LegacyDb.SCREENINGS.get((String) b[0]);
                @SuppressWarnings("unchecked")
                Set<String> taken = (Set<String>) s[6];
                for (String seat : (String[]) b[3]) {
                    taken.remove(seat);
                }
                notifications.bookingExpired((String) b[1], e.getKey());
            }
        }
    }

    public String use(String bookingId) {
        Object[] b = LegacyDb.BOOKINGS.get(bookingId);
        if (b == null) {
            return "ERROR: no booking";
        }
        if ((Integer) b[7] != 1) {
            return "ERROR: not paid";
        }
        b[7] = 2;
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
        for (Object[] b : LegacyDb.BOOKINGS.values()) {
            int status = (Integer) b[7];
            if (status != 1 && status != 2) {
                continue;
            }
            Object[] s = LegacyDb.SCREENINGS.get((String) b[0]);
            if (!((LocalDateTime) s[2]).toLocalDate().equals(day)) {
                continue;
            }
            double[] row = byTitle.computeIfAbsent((String) s[0], k -> new double[2]);
            row[0] = row[0] + ((String[]) b[3]).length;
            row[1] = row[1] + (Double) b[10];
            fees = fees + ((Double) b[6] - (Double) b[10]);
            tickets = tickets + ((String[]) b[3]).length;
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
        for (Object[] b : LegacyDb.BOOKINGS.values()) {
            int status = (Integer) b[7];
            Object[] s = LegacyDb.SCREENINGS.get((String) b[0]);
            if ((status == 1 || status == 2) && s[0].equals(title)) {
                revenue = revenue + (Double) b[10];
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
