package pl.training.workshop.m7.s01_breakdependencies.step3;

/** Statyczny klient SMTP. Poza produkcją serwer jest nieosiągalny. */
public final class ReminderMailer {
    private ReminderMailer() {
    }

    public static void send(String to, String subject, String body) {
        throw new IllegalStateException("SMTP smtp.kino.pl niedostepny (" + to + ")");
    }
}
