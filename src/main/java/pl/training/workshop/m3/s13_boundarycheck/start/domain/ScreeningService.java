package pl.training.workshop.m3.s13_boundarycheck.start.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import pl.training.workshop.m3.s13_boundarycheck.start.adapter.ScreeningRow;

/**
 * Start: klasa domeny miesza politykę cenową z mapowaniem na wiersz bazy.
 * Naruszenie granicy: domain importuje adapter i java.sql. Diagnostyka spójności:
 * dwie grupy metod na rozłącznych polach (LCOM4 = 2) - dwa pojęcia w jednej klasie.
 */
public final class ScreeningService {
    private final BigDecimal basePrice;
    private final BigDecimal morningDiscount;
    private final String table;

    public ScreeningService(BigDecimal basePrice, BigDecimal morningDiscount, String table) {
        this.basePrice = basePrice;
        this.morningDiscount = morningDiscount;
        this.table = table;
    }

    public BigDecimal price(Screening screening) {
        return isMorning(screening) ? basePrice.subtract(morningDiscount) : basePrice;
    }

    private boolean isMorning(Screening screening) {
        return screening.start().getHour() < 12;
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
