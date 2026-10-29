package pl.training.workshop.exercises.e02_hallmap.step3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;
import pl.training.workshop.exercises.e02_hallmap.SectionSpec;

/** Krok 3: sala - korzeń drzewa i kolekcja miejsc; publiczna, bo buduje ją też test rozwiązania. */
public final class Hall extends HallGroup implements Iterable<Seat> {
    private final String name;

    private Hall(String name, List<Section> sections) {
        super(sections);
        this.name = name;
    }

    public static Hall of(HallSpec spec) {
        if (spec.sections().isEmpty()) {
            throw new IllegalArgumentException("hall " + spec.name() + " has no sections");
        }
        List<Section> sections = new ArrayList<>();
        for (SectionSpec sectionSpec : spec.sections()) {
            sections.add(Section.of(sectionSpec));
        }
        return new Hall(spec.name(), sections);
    }

    public String name() {
        return name;
    }

    @Override
    public Iterator<Seat> iterator() {
        return new SeatIterator(this);
    }
}
