package pl.training.workshop.m8.s10_qualitygate.sample.clean;

/** Próbka czystego kodu domeny: bramka nie może tu niczego zgłosić (brak fałszywych alarmów). */
public final class PriceTable {
    private static final int VIP_FROM_ROW = 10;

    public int basePrice(String format) {
        return switch (format) {
            case "IMAX" -> 40;
            case "3D" -> 32;
            default -> 25;
        };
    }

    public int vipSurcharge(int row) {
        return row >= VIP_FROM_ROW ? 10 : 0;
    }
}
