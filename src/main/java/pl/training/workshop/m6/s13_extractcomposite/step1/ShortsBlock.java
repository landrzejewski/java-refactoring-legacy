package pl.training.workshop.m6.s13_extractcomposite.step1;

/** Krok 1: pole children, add i children() podciągnięte do CompositeProgramItem. */
public final class ShortsBlock extends CompositeProgramItem {
    private final String name;

    public ShortsBlock(String name) {
        this.name = name;
    }

    @Override
    public int minutes() {
        int total = 0;
        for (ProgramItem child : children()) {
            total += child.minutes();
        }
        return total;
    }

    @Override
    public String describe() {
        return "Blok " + name + " (" + minutes() + " min) "
                + children().stream().map(ProgramItem::describe).toList();
    }
}
