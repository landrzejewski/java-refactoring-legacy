package pl.training.workshop.m8.s12_expandcontract;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Tabela rezerwacji po migracji schematu "expand": obok starej kolumny {@code csv} jest nowa,
 * dopuszczająca null kolumna {@code payload}. Samo dodanie kolumny nic nie psuje - stary kod
 * jej nie zna. Usunięcie kolumny csv to osobna migracja po zamknięciu okna wycofania.
 */
public final class BookingTable {
    /** Wiersz: stara kolumna csv i nowa kolumna payload (każda może być null). */
    public record Row(String csv, String payload) {
        public static Row withPayload(String payload) {
            return new Row(null, payload);
        }
    }

    private final Map<String, Row> rows = new TreeMap<>();

    public void put(String id, Row row) {
        rows.put(id, row);
    }

    public Optional<Row> get(String id) {
        return Optional.ofNullable(rows.get(id));
    }

    public List<String> ids() {
        return List.copyOf(rows.keySet());
    }
}
