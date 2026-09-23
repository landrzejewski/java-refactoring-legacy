package pl.training.workshop.m5.s02_pullupfield.start;

/** Start: klient tworzy bilet normalny w dwóch krokach (konstruktor + setter). */
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
