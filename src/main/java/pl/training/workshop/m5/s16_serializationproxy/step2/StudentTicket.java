package pl.training.workshop.m5.s16_serializationproxy.step2;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * Krok 2: Serialization Proxy (Effective Java) - do strumienia trafia płaski record SerializedForm,
 * a odczyt przechodzi przez publiczny konstruktor. Hierarchia klasy przestaje być formatem danych.
 * serialVersionUID = 2L to świadoma deklaracja nowego formatu: stare bajty są odrzucane głośno
 * (InvalidClassException) zamiast po cichu gubić pola - ich migracja to osobne zadanie.
 */
public final class StudentTicket extends Ticket implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private final String studentId;

    public StudentTicket(String title, String seat, String studentId) {
        super(title, seat);
        this.studentId = studentId;
    }

    public String describe() {
        return title() + " " + seat() + " (legitymacja " + studentId + ")";
    }

    @Serial
    private Object writeReplace() {
        return new SerializedForm(title(), seat(), studentId);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("StudentTicket czytamy tylko przez SerializedForm");
    }

    private record SerializedForm(String title, String seat, String studentId) implements Serializable {
        @Serial
        private Object readResolve() {
            return new StudentTicket(title, seat, studentId);
        }
    }
}
