package pl.training.workshop.m5.s16_serializationproxy.start;

import java.io.Serial;
import java.io.Serializable;

/**
 * Start: bilet zapisywany serializacją Javy (np. sesja HTTP, cache, kolejka). Postać strumienia
 * to nazwa klasy + pola KAŻDEGO poziomu hierarchii osobno - hierarchia jest częścią formatu danych.
 */
public final class StudentTicket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String seat;
    private final String studentId;

    public StudentTicket(String title, String seat, String studentId) {
        this.title = title;
        this.seat = seat;
        this.studentId = studentId;
    }

    public String describe() {
        return title + " " + seat + " (legitymacja " + studentId + ")";
    }
}
