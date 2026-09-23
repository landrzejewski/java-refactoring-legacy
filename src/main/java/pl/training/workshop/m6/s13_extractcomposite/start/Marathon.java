package pl.training.workshop.m6.s13_extractcomposite.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Start: kontener filmów. Obsługa dzieci (lista, add, children, suma, opis) jest skopiowana
 * w ShortsBlock. Różni się tylko reguła czasu: 15 minut przerwy między pozycjami.
 */
public final class Marathon implements ProgramItem {
    private final String name;
    private final List<ProgramItem> children = new ArrayList<>();

    public Marathon(String name) {
        this.name = name;
    }

    public void add(ProgramItem child) {
        children.add(Objects.requireNonNull(child, "child"));
    }

    public List<ProgramItem> children() {
        return List.copyOf(children);
    }

    @Override
    public int minutes() {
        int total = 0;
        for (ProgramItem child : children) {
            total += child.minutes();
        }
        return children.isEmpty() ? 0 : total + 15 * (children.size() - 1);
    }

    @Override
    public String describe() {
        return "Maraton " + name + " (" + minutes() + " min) "
                + children.stream().map(ProgramItem::describe).toList();
    }
}
