package pl.training.workshop.m5.s16_serializationproxy.step1;

import java.io.Serial;
import java.io.Serializable;

/**
 * Krok 1: Extract Superclass + Pull Up Field (title, seat) - poprawne dla kodu, groźne dla danych.
 * Pola przeszły do innego segmentu strumienia: stare bajty nie mają danych dla poziomu Ticket.
 */
public abstract class Ticket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String seat;

    protected Ticket(String title, String seat) {
        this.title = title;
        this.seat = seat;
    }

    public String title() {
        return title;
    }

    public String seat() {
        return seat;
    }
}
