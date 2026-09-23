package pl.training.workshop.m8.s01_branchbyabstraction.start;

import java.util.Locale;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;
import pl.training.workshop.m8.s01_branchbyabstraction.Screening;

/**
 * Start: cennik skopiowany z CinemaManager.book() jest wpleciony w potwierdzenie rezerwacji.
 * Chcemy go wymienić na nową implementację (Money, czytelne reguły), ale nie ma szwu:
 * double, magiczne kody formatu i typu biletu, wszystko w jednej metodzie.
 */
public final class BookingService {
    public String confirm(BookingRequest request) {
        Screening s = request.screening();
        double sum = 0;
        for (int i = 0; i < request.seats().size(); i++) {
            double p = 0;
            int f = s.format();
            if (f == 1) {
                p = 25.00;
            } else if (f == 2) {
                p = 32.00;
            } else if (f == 3) {
                p = 40.00;
            }
            String type = request.types().get(i);
            if (type.equals("S")) {
                p = p - p * 0.25;
            } else if (type.equals("E")) {
                p = p - p * 0.30;
            } else if (type.equals("C")) {
                p = p - p * 0.40;
            }
            if (s.start().getHour() < 12) {
                p = p - 5;
            }
            if (Integer.parseInt(request.seats().get(i).substring(1)) >= s.vipFromRow()) {
                p = p + 10;
            }
            if (f == 2 && !request.ownGlasses()) {
                p = p + 3;
            }
            sum = sum + p;
        }
        if (request.seats().size() >= 10) {
            sum = sum - sum * 0.10;
        }
        sum = Math.round(sum * 100) / 100.0;
        double total = sum;
        if (request.web()) {
            total = total + 2.00 * request.seats().size();
        }
        return s.title() + ": " + String.join(",", request.seats())
                + " - do zaplaty " + String.format(Locale.ROOT, "%.2f", total);
    }
}
