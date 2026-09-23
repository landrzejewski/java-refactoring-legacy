package pl.training.workshop.m5.s02_pullupfield.start;

/** Start: to samo znaczenie co "seat", ale inna nazwa. studentId to inne pojęcie - zostaje tutaj. */
public final class StudentTicket extends Ticket {
    private final String seatCode;
    private final String studentId;

    public StudentTicket(String seatCode, String studentId) {
        this.seatCode = seatCode;
        this.studentId = studentId;
    }

    public String seatCode() {
        return seatCode;
    }

    @Override
    public String describe() {
        return "STUDENT " + seatCode + " (legitymacja " + studentId + ")";
    }
}
