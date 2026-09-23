package pl.training.workshop.m5.s05_extractsubclass.step4;

/** Krok 4: bez zmian - klient przeżył całą ekstrakcję bez modyfikacji od kroku 1. */
public final class Programme {
    public String line(String title, String format, String guest) {
        Screening screening = guest == null
                ? Screening.regular(title, format)
                : Screening.premiere(title, format, guest);
        return screening.describe() + " | " + screening.price();
    }
}
