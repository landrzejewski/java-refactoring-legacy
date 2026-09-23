package pl.training.workshop.m7.s13_godclass.step3;

import java.time.LocalDateTime;

/**
 * Krok 1: cennik wydzielony z CinemaManager.book(). Kod przeniesiony dosłownie
 * (łącznie z double i kolejnością operacji) - poprawianie typu pieniędzy to osobna decyzja (s14).
 */
final class PricingService {
    // format: 1 = 2D, 2 = 3D, 3 = IMAX; typ biletu: N, S, E, C

    double ticketsSum(int format, LocalDateTime start, int vipFromRow,
            String[] seats, String[] types, boolean ownGlasses) {
        double sum = 0;
        for (int i = 0; i < seats.length; i++) {
            double p = 0;
            if (format == 1) {
                p = 25.00;
            } else if (format == 2) {
                p = 32.00;
            } else if (format == 3) {
                p = 40.00;
            }
            if (types[i].equals("S")) {
                p = p - p * 0.25;
            } else if (types[i].equals("E")) {
                p = p - p * 0.30;
            } else if (types[i].equals("C")) {
                p = p - p * 0.40;
            }
            if (start.getHour() < 12) {
                p = p - 5;
            }
            if (Integer.parseInt(seats[i].substring(1)) >= vipFromRow) {
                p = p + 10;
            }
            if (format == 2 && !ownGlasses) {
                p = p + 3;
            }
            sum = sum + p;
        }
        if (seats.length >= 10) {
            sum = sum - sum * 0.10;
        }
        return Math.round(sum * 100) / 100.0;
    }

    double bookingFee(boolean web, int tickets) {
        return web ? 2.00 * tickets : 0;
    }
}
