package pl.training.workshop.m6.s06_builder.start;

import java.util.ArrayList;
import java.util.List;

/** Start - mutowalny węzeł: sala z listą seansów. */
public final class Hall {
    private final String name;
    private final List<Screening> screenings = new ArrayList<>();

    public Hall(String name) {
        this.name = name;
    }

    public void add(Screening screening) {
        screenings.add(screening);
    }

    public int size() {
        return screenings.size();
    }

    public String render() {
        StringBuilder text = new StringBuilder(name).append('\n');
        if (screenings.isEmpty()) {
            text.append("  (brak seansow)\n");
        }
        screenings.forEach(screening -> text.append(screening.render()));
        return text.toString();
    }
}
