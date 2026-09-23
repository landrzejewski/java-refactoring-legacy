package pl.training.workshop.m5.s02_pullupfield.step1;

/** Krok 1: bez zmian - klient nie używał seatCode(). */
public final class BoxOffice {
    public String describe(String kind, String seat, String studentId) {
        return switch (kind) {
            case "STUDENT" -> new StudentTicket(seat, studentId).describe();
            case "VIP" -> new VipTicket(seat).describe();
            default -> {
                StandardTicket ticket = new StandardTicket();
                ticket.setSeat(seat);
                yield ticket.describe();
            }
        };
    }
}
