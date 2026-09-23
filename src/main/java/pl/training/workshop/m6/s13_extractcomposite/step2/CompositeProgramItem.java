package pl.training.workshop.m6.s13_extractcomposite.step2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Krok 2: Pull Up Method - suma czasu dzieci i szkielet opisu też w nadklasie. Podklasy
 * dostarczają tylko to, czym naprawdę się różnią: etykietę i regułę przerw.
 */
public abstract class CompositeProgramItem implements ProgramItem {
    private final String name;
    private final List<ProgramItem> children = new ArrayList<>();

    protected CompositeProgramItem(String name) {
        this.name = name;
    }

    public final void add(ProgramItem child) {
        children.add(Objects.requireNonNull(child, "child"));
    }

    public final List<ProgramItem> children() {
        return List.copyOf(children);
    }

    protected final int childrenMinutes() {
        int total = 0;
        for (ProgramItem child : children) {
            total += child.minutes();
        }
        return total;
    }

    @Override
    public final String describe() {
        return label() + " " + name + " (" + minutes() + " min) "
                + children.stream().map(ProgramItem::describe).toList();
    }

    protected abstract String label();
}
