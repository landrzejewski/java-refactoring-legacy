package pl.training.workshop.m5.s05_extractsubclass.step2;

/** Krok 2: bez zmian - klient nie wie, że fabryka zwraca podklasę. */
public final class Programme {
    public String line(String title, String format, String guest) {
        Screening screening = guest == null
                ? Screening.regular(title, format)
                : Screening.premiere(title, format, guest);
        return screening.describe() + " | " + screening.price();
    }
}
