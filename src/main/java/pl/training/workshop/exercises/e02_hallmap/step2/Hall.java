package pl.training.workshop.exercises.e02_hallmap.step2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;
import pl.training.workshop.exercises.e02_hallmap.SectionSpec;

/**
 * Krok 2: sala jest kolekcją miejsc - da się po niej przejść pętlą for-each. Typowany dostęp
 * do sektorów zniknął, bo klient już go nie potrzebuje.
 */
public final class Hall extends HallGroup implements Iterable<Seat> {
    private final String name;

    private Hall(String name, List<Section> sections) {
        super(sections);
        this.name = name;
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

    @Override
    public Iterator<Seat> iterator() {
        return new SeatIterator(this);
    }
}
