package pl.training.workshop.m7.s15_behaviourvector.start;

/** Statyczny klient SMTP. Z testu nie widać, co i do kogo wysłał. */
public final class CinemaMailer {
    private CinemaMailer() {
    }

    public static void send(String to, String text) {
        // produkcyjnie: SMTP smtp.kino.pl
    }
}
