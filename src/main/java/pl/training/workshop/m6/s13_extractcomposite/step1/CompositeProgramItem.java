package pl.training.workshop.m6.s13_extractcomposite.step1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Krok 1: Extract Superclass - wspólna obsługa dzieci (pole, add, children) w jednym miejscu. */
public abstract class CompositeProgramItem implements ProgramItem {
    private final List<ProgramItem> children = new ArrayList<>();

    public final void add(ProgramItem child) {
        children.add(Objects.requireNonNull(child, "child"));
    }

    public final List<ProgramItem> children() {
        return List.copyOf(children);
    }
}
