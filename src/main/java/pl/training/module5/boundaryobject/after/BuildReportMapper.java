package pl.training.module5.boundaryobject.after;

import java.util.Locale;
import java.util.Map;

/**
 * Jedyne miejsce, które zna format ładunku CI. Tłumaczy go na {@link BuildReport}
 * albo odrzuca na granicy z jednym, czytelnym wyjątkiem.
 */
final class BuildReportMapper {
    private BuildReportMapper() {
    }

    static BuildReport fromPayload(Map<String, String> payload) {
        if (payload == null) {
            throw new IllegalArgumentException("invalid CI payload: missing");
        }
        try {
            return new BuildReport(
                    required(payload, "id"),
                    BuildReport.Status.valueOf(required(payload, "status").toUpperCase(Locale.ROOT)),
                    Double.parseDouble(required(payload, "coverage")),
                    Integer.parseInt(required(payload, "failed")),
                    required(payload, "commit").toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "invalid CI payload: " + exception.getMessage(), exception);
        }
    }

    private static String required(Map<String, String> payload, String key) {
        String value = payload.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("missing " + key);
        }
        return value.trim();
    }
}
