package pl.training.workshop.m5.s05_extractsubclass.start;

/** Start: klient importujący repertuar - guest == null oznacza zwykły seans. */
public final class Programme {
    public String line(String title, String format, String guest) {
        Screening screening = new Screening(title, format, guest != null, guest);
        return screening.describe() + " | " + screening.price();
    }
}
