package pl.training.workshop.m5.s02_pullupfield.step2;

/** Krok 2: klient tworzy każdy bilet jednym wywołaniem konstruktora. */
public final class BoxOffice {
    public String describe(String kind, String seat, String studentId) {
        return switch (kind) {
            case "STUDENT" -> new StudentTicket(seat, studentId).describe();
            case "VIP" -> new VipTicket(seat).describe();
            default -> new StandardTicket(seat).describe();
        };
    }
}
