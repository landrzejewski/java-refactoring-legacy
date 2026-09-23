package pl.training.workshop.m3.s13_boundarycheck.step2.adapter;

import java.sql.Timestamp;

import pl.training.workshop.m3.s13_boundarycheck.step2.domain.Screening;

/**
 * Krok 2 (rozwiązanie): Move Class do adapter. Zależność biegnie adapter -&gt; domain,
 * domena nie zna java.sql ani wiersza tabeli. Test granicy zielony.
 */
public final class ScreeningRowMapper {
    private final String table;

    public ScreeningRowMapper(String table) {
        this.table = table;
    }

    public ScreeningRow toRow(Screening screening) {
        return new ScreeningRow(table, screening.title(), Timestamp.valueOf(screening.start()));
    }

    public Screening fromRow(ScreeningRow row) {
        if (!row.table().equals(table)) {
            throw new IllegalArgumentException("obca tabela: " + row.table());
        }
        return new Screening(row.title(), row.start().toLocalDateTime());
    }
}
