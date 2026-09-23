package pl.training.workshop.m5.s16_serializationproxy.step1;

import java.io.Serial;

/**
 * Krok 1: ten sam serialVersionUID = 1L, więc deserializacja starych danych NIE rzuci wyjątku -
 * title i seat po cichu będą null. Stały UID nie migruje stanu.
 */
public final class StudentTicket extends Ticket {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String studentId;

    public StudentTicket(String title, String seat, String studentId) {
        super(title, seat);
        this.studentId = studentId;
    }

    public String describe() {
        return title() + " " + seat() + " (legitymacja " + studentId + ")";
    }
}
