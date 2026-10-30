package pl.training.workshop.exercises.e02_hallmap.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.RowSpec;
import pl.training.workshop.exercises.e02_hallmap.SectionSpec;

/** Start: sektor to nazwa i lista rzędów - kolejny poziom, przez który klient musi przejść pętlą. */
public final class Section extends HallGroup {
    private final String name;
    private final List<Row> rows;

    private Section(String name, List<Row> rows) {
        super(rows);
        this.name = name;
        this.rows = List.copyOf(rows);
    }

    static Section of(SectionSpec spec) {
        if (spec.rows().isEmpty()) {
            throw new IllegalArgumentException("section " + spec.name() + " has no rows");
        }
        List<Row> rows = new ArrayList<>();
        for (RowSpec rowSpec : spec.rows()) {
            rows.add(Row.of(rowSpec));
        }
        return new Section(spec.name(), rows);
    }

    String name() {
        return name;
    }

    List<Row> rows() {
        return rows;
    }
}
