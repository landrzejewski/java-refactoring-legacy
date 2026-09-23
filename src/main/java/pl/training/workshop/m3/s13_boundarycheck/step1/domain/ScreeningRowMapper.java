package pl.training.workshop.m3.s13_boundarycheck.step1.domain;

import java.sql.Timestamp;

import pl.training.workshop.m3.s13_boundarycheck.step1.adapter.ScreeningRow;

/**
 * Krok 1: Extract Class - mapowanie na wiersz jako osobna, spójna klasa (LCOM4 = 1).
 * Wciąż leży w domain, więc test granicy nadal jest czerwony: spójność poprawiona,
 * kierunek zależności jeszcze nie.
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
