package pl.training.workshop.m5.s02_pullupfield.step3;

/** Krok 3: bez zmian - klient nie zauważył przeniesienia pola. */
public final class BoxOffice {
    public String describe(String kind, String seat, String studentId) {
        return switch (kind) {
            case "STUDENT" -> new StudentTicket(seat, studentId).describe();
            case "VIP" -> new VipTicket(seat).describe();
            default -> new StandardTicket(seat).describe();
        };
    }
}
