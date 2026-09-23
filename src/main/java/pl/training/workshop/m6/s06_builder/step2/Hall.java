package pl.training.workshop.m6.s06_builder.step2;

import java.util.List;

/** Krok 2: niemutowalny węzeł - lista kopiowana w konstruktorze, bez add. */
public record Hall(String name, List<Screening> screenings) {
    public Hall {
        screenings = List.copyOf(screenings);
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
