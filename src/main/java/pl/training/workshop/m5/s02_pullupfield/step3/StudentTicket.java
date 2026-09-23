package pl.training.workshop.m5.s02_pullupfield.step3;

/** Krok 3: seat w bazie; studentId ma inne znaczenie, więc zostaje w podklasie. */
public final class StudentTicket extends Ticket {
    private final String studentId;

    public StudentTicket(String seat, String studentId) {
        super(seat);
        this.studentId = studentId;
    }

    @Override
    public String describe() {
        return "STUDENT " + seat() + " (legitymacja " + studentId + ")";
    }
}
