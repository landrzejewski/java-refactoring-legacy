package pl.training.workshop.m6.s13_extractcomposite.step2;

/** Krok 2: bez zmian - liść - pojedynczy film. */
public record Film(String title, int minutes) implements ProgramItem {
    @Override
    public String describe() {
        return title + " (" + minutes + " min)";
    }
}
