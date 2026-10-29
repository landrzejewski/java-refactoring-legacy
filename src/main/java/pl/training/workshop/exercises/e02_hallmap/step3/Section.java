package pl.training.workshop.exercises.e02_hallmap.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.RowSpec;
import pl.training.workshop.exercises.e02_hallmap.SectionSpec;

/** Krok 3: sektor - bez zmian. */
public final class Section extends HallGroup {
    private final String name;

    private Section(String name, List<Row> rows) {
        super(rows);
        this.name = name;
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
}
