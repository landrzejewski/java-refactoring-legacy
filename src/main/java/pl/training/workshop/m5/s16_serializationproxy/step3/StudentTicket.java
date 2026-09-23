package pl.training.workshop.m5.s16_serializationproxy.step3;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * Krok 3: bez zmian - proxy serializacji z kroku 2.
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
