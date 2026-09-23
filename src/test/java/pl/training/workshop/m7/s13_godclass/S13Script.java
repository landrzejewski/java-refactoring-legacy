package pl.training.workshop.m7.s13_godclass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Scenariusz "jednego dnia kina" - ten sam co w golden master legacy
 * (src/test/java/pl/training/workshop/legacy/CinemaManagerScript.java), ale uruchamiany
 * przez CinemaUnderTest, żeby dało się go puścić na start i na każdym kroku kampanii.
 */
final class S13Script {
    private S13Script() {
    }

    static String run(CinemaUnderTest cinema) {
        cinema.reset();
        List<String> log = new ArrayList<>();
        LocalDateTime[] now = {LocalDateTime.of(2026, 3, 9, 9, 0)};
        cinema.clock(() -> now[0]);
        try {
            cinema.addScreening("S1", "Diuna", 3, LocalDateTime.of(2026, 3, 10, 20, 0), 12, 10, 10);
            cinema.addScreening("S2", "Kraina Lodu", 2, LocalDateTime.of(2026, 3, 10, 11, 0), 8, 8, 7);
            cinema.addScreening("S3", "Amator", 1, LocalDateTime.of(2026, 3, 10, 18, 30), 10, 12, 9);

            String b1 = cinema.book("S1", "anna@kino.pl", "600100200",
                    new String[] {"A5", "B5", "C10"}, new String[] {"N", "S", "E"}, true, false);
            String b2 = cinema.book("S2", "jan@kino.pl", null,
                    new String[] {"A1", "B1", "C1", "D7"}, new String[] {"N", "C", "C", "N"}, false, false);
            String b3 = cinema.book("S2", "ola@kino.pl", "600300400",
                    new String[] {"E2", "F2"}, new String[] {"S", "S"}, true, true);
            String[] groupSeats = {"A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1", "I1", "J1"};
            String[] groupTypes = {"C", "C", "C", "C", "C", "C", "C", "C", "N", "N"};
            String b4 = cinema.book("S3", "szkola@kino.pl", null, groupSeats, groupTypes, true, false);
            String b5 = cinema.book("S3", "piotr@kino.pl", null,
                    new String[] {"K9"}, new String[] {"N"}, false, false);
            log.add("book: " + b1 + " " + b2 + " " + b3 + " " + b4 + " " + b5);
            log.add("book taken: " + cinema.book("S1", "x@kino.pl", null,
                    new String[] {"A5"}, new String[] {"N"}, true, false));
            log.add("book no seat: " + cinema.book("S1", "x@kino.pl", null,
                    new String[] {"Z99"}, new String[] {"N"}, true, false));
            log.add("book mismatch: " + cinema.book("S1", "x@kino.pl", null,
                    new String[] {"A1"}, new String[] {}, true, false));
            log.add("book no screening: " + cinema.book("S9", "x@kino.pl", null,
                    new String[] {"A1"}, new String[] {"N"}, true, false));

            log.add("pay b1: " + cinema.pay(b1, "4111111111111111"));
            log.add("pay b1 again: " + cinema.pay(b1, "4111111111111111"));
            log.add("pay b2 declined: " + cinema.pay(b2, "4111111111110000"));
            log.add("pay b2: " + cinema.pay(b2, "5555444433331111"));
            log.add("pay b4: " + cinema.pay(b4, "4000123412341234"));

            now[0] = LocalDateTime.of(2026, 3, 9, 9, 20);
            cinema.expireOld();
            log.add("pay b3 expired: " + cinema.pay(b3, "4111111111111111"));

            log.add("cancel b1 early: " + cinema.cancel(b1));
            now[0] = LocalDateTime.of(2026, 3, 10, 10, 0);
            log.add("cancel b2 late: " + cinema.cancel(b2));
            log.add("cancel b2 again: " + cinema.cancel(b2));
            log.add("use b4: " + cinema.use(b4));
            log.add("use b5 unpaid: " + cinema.use(b5));

            log.add("points anna: " + cinema.loyaltyPoints("anna@kino.pl"));
            log.add("points szkola: " + cinema.loyaltyPoints("szkola@kino.pl"));
            log.add("free S1: " + cinema.freeSeats("S1").size());
            log.add(cinema.dailyReport(LocalDate.of(2026, 3, 10)).strip());
            log.add(cinema.settlement("Amator", 1));
            log.add(cinema.settlement("Diuna", 2));
            log.addAll(cinema.sentMessages());
            log.addAll(cinema.gatewayOperations());
            return String.join("\n", log) + "\n";
        } finally {
            cinema.clock(LocalDateTime::now);
            cinema.reset();
        }
    }
}
