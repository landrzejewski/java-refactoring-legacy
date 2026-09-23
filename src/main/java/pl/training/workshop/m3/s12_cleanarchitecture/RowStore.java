package pl.training.workshop.m3.s12_cleanarchitecture;

import java.util.ArrayList;
import java.util.List;

/**
 * Świat zewnętrzny sceny (stabilny): "baza danych" z wierszami Object[].
 * Niedostępna baza rzuca {@link IllegalStateException} przy zapisie.
 */
public final class RowStore {
    private final boolean available;
    private final List<Object[]> rows = new ArrayList<>();

    public RowStore(boolean available) {
        this.available = available;
    }

    /** Zapisuje wiersz i zwraca wygenerowany identyfikator R-n. */
    public String insert(Object[] columns) {
        if (!available) {
            throw new IllegalStateException("baza niedostepna");
        }
        rows.add(columns.clone());
        return "R-" + rows.size();
    }

    public List<String> dump() {
        List<String> result = new ArrayList<>();
        for (Object[] row : rows) {
            StringBuilder line = new StringBuilder();
            for (Object column : row) {
                line.append(line.isEmpty() ? "" : ";").append(column);
            }
            result.add(line.toString());
        }
        return result;
    }
}
