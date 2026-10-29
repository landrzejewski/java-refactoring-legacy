package pl.training.workshop.exercises.e02_hallmap.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;
import pl.training.workshop.exercises.e02_hallmap.SectionSpec;

/** Krok 1: sala jest węzłem drzewa - pojemność i dopłaty dziedziczy z {@link HallGroup}. */
public final class Hall extends HallGroup {
    private final String name;
    private final List<Section> sections;

    private Hall(String name, List<Section> sections) {
        super(sections);
        this.name = name;
        this.sections = List.copyOf(sections);
    }

    static Hall of(HallSpec spec) {
        if (spec.sections().isEmpty()) {
            throw new IllegalArgumentException("hall " + spec.name() + " has no sections");
        }
        List<Section> sections = new ArrayList<>();
        for (SectionSpec sectionSpec : spec.sections()) {
            sections.add(Section.of(sectionSpec));
        }
        return new Hall(spec.name(), sections);
    }

    String name() {
        return name;
    }

    List<Section> sections() {
        return sections;
    }
}
