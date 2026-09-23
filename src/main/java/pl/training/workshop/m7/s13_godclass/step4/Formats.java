package pl.training.workshop.m7.s13_godclass.step4;

import java.util.Locale;

/** Krok 2: format kwot ("114.00") potrzebny i CinemaManager, i NotificationService - jeden właściciel. */
final class Formats {
    private Formats() {
    }

    static String amount(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
