package pl.training.workshop.m5.s27_templatetostrategy.step2;

/**
 * Stabilny punkt wejścia sceny: raport dla nazwy okresu (DAILY, WEEKLY) i formatu (CSV, TEXT).
 * Krok 2: bez zmian - cztery podklasy są już tylko konfiguracją dwóch strategii.
 */
public final class Reports {
    private Reports() {
    }

    public static ScreeningReport of(String period, String format) {
        return switch (period) {
            case "DAILY" -> switch (format) {
                case "CSV" -> new DailyCsvReport();
                case "TEXT" -> new DailyTextReport();
                default -> throw unknownFormat(format);
            };
            case "WEEKLY" -> switch (format) {
                case "CSV" -> new WeeklyCsvReport();
                case "TEXT" -> new WeeklyTextReport();
                default -> throw unknownFormat(format);
            };
            default -> throw new IllegalArgumentException("unknown period: " + period);
        };
    }

    private static IllegalArgumentException unknownFormat(String format) {
        return new IllegalArgumentException("unknown format: " + format);
    }
}
