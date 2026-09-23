package pl.training.workshop.m5.s05_extractsubclass.step3;

/** Krok 3: bez zmian. */
public final class Programme {
    public String line(String title, String format, String guest) {
        Screening screening = guest == null
                ? Screening.regular(title, format)
                : Screening.premiere(title, format, guest);
        return screening.describe() + " | " + screening.price();
    }
}
