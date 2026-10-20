package pl.training.workshop.m5.s13_extractcomposite.start;

/** Start: liść - pojedynczy film. */
public record Film(String title, int minutes) implements ProgramItem {
    @Override
    public String describe() {
        return title + " (" + minutes + " min)";
    }
}
