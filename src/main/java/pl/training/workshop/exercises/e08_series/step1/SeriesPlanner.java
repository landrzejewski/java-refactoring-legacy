package pl.training.workshop.exercises.e08_series.step1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.training.workshop.exercises.e08_series.SeriesRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: planer nie kopiuje już seansu pole po polu. Kolejny tydzień to {@code template.withStart(...)},
 * premiera to {@code withSurcharge(...)} na kopii, a inna sala to {@code withHall(...)}.
 * Dopłata premierowa zostaje w pierwszym tygodniu (w start wyciekała do wszystkich).
 * Domyślna sala przenosi się do szablonu, bo kopia już jej nie uzupełnia.
 */
public final class SeriesPlanner {
    private static final Money VIP = Money.of("10.00");
    private static final Money GLASSES_3D = Money.of("3.00");
    private static final Money PREMIERE = Money.of("5.00");

    public List<Screening> plan(SeriesRequest request) {
        if (request.weeks() < 1) {
            throw new IllegalArgumentException("weeks must be positive");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (!List.of("2D", "3D", "IMAX").contains(request.format())) {
            throw new IllegalArgumentException("unknown format: " + request.format());
        }
        Money base = request.basePrice() != null ? request.basePrice() : priceFor(request.format());
        if (base.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("base price must not be negative");
        }
        Map<String, Money> surcharges = new LinkedHashMap<>();
        surcharges.put("VIP", VIP);
        if (request.format().equals("3D")) {
            surcharges.put("OKULARY", GLASSES_3D);
        }
        boolean dubbing = request.dubbing() != null && request.dubbing();
        int ageLimit = request.ageLimit() != null ? request.ageLimit() : 12;
        Screening template = new Screening(request.title(), request.format(),
                request.hall() != null ? request.hall() : "A",
                request.firstStart(), base, surcharges, !dubbing, dubbing, ageLimit);

        List<Screening> series = new ArrayList<>();
        for (int week = 0; week < request.weeks(); week++) {
            Screening copy = template.withStart(template.start().plusWeeks(week));
            if (week == 0 && request.premiereWeek()) {
                copy = copy.withSurcharge("PREMIERA", PREMIERE);
            }
            series.add(copy);
        }
        if (request.lastWeekHall() != null) {
            series.set(series.size() - 1, series.getLast().withHall(request.lastWeekHall()));
        }
        return series;
    }

    private static Money priceFor(String format) {
        return switch (format) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("unknown format: " + format);
        };
    }
}
