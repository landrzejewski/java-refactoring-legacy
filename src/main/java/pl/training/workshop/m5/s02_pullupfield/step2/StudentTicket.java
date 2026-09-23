package pl.training.workshop.m5.s02_pullupfield.step2;

/** Krok 2: bez zmian. */
public final class StudentTicket extends Ticket {
    private final String seat;
    private final String studentId;

    public StudentTicket(String seat, String studentId) {
        this.seat = seat;
        this.studentId = studentId;
    }

    public String seat() {
        return seat;
    }

    @Override
    public String describe() {
        return "STUDENT " + seat + " (legitymacja " + studentId + ")";
    }
}
