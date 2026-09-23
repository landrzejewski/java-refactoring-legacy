package pl.training.workshop.m6.s13_extractcomposite.step1;

import java.util.List;

/** Krok 1: pole children, add i children() podciągnięte do CompositeProgramItem. */
public final class Marathon extends CompositeProgramItem {
    private final String name;

    public Marathon(String name) {
        this.name = name;
    }

    @Override
    public int minutes() {
        List<ProgramItem> children = children();
        int total = 0;
        for (ProgramItem child : children) {
            total += child.minutes();
        }
        return children.isEmpty() ? 0 : total + 15 * (children.size() - 1);
    }

    @Override
    public String describe() {
        return "Maraton " + name + " (" + minutes() + " min) "
                + children().stream().map(ProgramItem::describe).toList();
    }
}
