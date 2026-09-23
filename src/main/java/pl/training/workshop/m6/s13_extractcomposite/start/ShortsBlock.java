package pl.training.workshop.m6.s13_extractcomposite.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Start: drugi kontener z tą samą obsługą dzieci - filmy krótkie lecą bez przerw. */
public final class ShortsBlock implements ProgramItem {
    private final String name;
    private final List<ProgramItem> children = new ArrayList<>();

    public ShortsBlock(String name) {
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
        return total;
    }

    @Override
    public String describe() {
        return "Blok " + name + " (" + minutes() + " min) "
                + children.stream().map(ProgramItem::describe).toList();
    }
}
