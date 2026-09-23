package pl.training.workshop.m3.s12_cleanarchitecture.start;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.training.workshop.m3.s12_cleanarchitecture.Outbox;
import pl.training.workshop.m3.s12_cleanarchitecture.RowStore;

/**
 * Start: kontroler "HTTP" robi wszystko - parsuje parametry, liczy cenę, zapisuje
 * wiersz Object[], publikuje komunikat i buduje odpowiedź. Reguły biznesowe
 * (cena, VIP, "najpierw zapis, potem powiadomienie") są splecione z formatem
 * żądania i kolumnami tabeli. Przypadku użycia nie da się wywołać bez HTTP i bazy.
 */
public final class ReservationController {
    private final RowStore db;
    private final Outbox outbox;

    public ReservationController(RowStore db, Outbox outbox) {
        this.db = db;
        this.outbox = outbox;
    }

    public String handle(Map<String, String> params) {
        String email = params.get("email");
        if (email == null || email.isBlank()) {
            return "400 brak email";
        }
        String format = params.getOrDefault("format", "2D");
        List<Integer> rows = Arrays.stream(params.getOrDefault("rows", "").split(","))
                .filter(s -> !s.isBlank()).map(Integer::parseInt).toList();
        if (rows.isEmpty()) {
            return "400 brak miejsc";
        }
        BigDecimal base = switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal total = new BigDecimal("0.00");
        for (int row : rows) {
            total = total.add(base);
            if (row >= 10) {
                total = total.add(new BigDecimal("10.00"));
            }
        }
        String id;
        try {
            id = db.insert(new Object[] {email, format, rows.size(), total});
        } catch (IllegalStateException e) {
            return "503 " + e.getMessage();
        }
        outbox.publish("reservation-created", id + ";" + email + ";" + total);
        return "201 " + id + " " + total;
    }
}
