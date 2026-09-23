package pl.training.workshop.m5.s05_extractsubclass.step1;

/** Krok 1: klient wybiera wariant przez nazwę fabryki, a nie przez flagę i null. */
public final class Programme {
    public String line(String title, String format, String guest) {
        Screening screening = guest == null
                ? Screening.regular(title, format)
                : Screening.premiere(title, format, guest);
        return screening.describe() + " | " + screening.price();
    }
}
