package pl.training.workshop.exercises.e08_series.start;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.training.workshop.exercises.e08_series.SeriesRequest;
import pl.training.workshop.shared.Money;

/**
 * Start: planer serii tygodniowej. Szablon powstaje dziewięcioargumentowym konstruktorem,
 * a każdy kolejny tydzień i przeniesienie do innej sali to ręczna kopia pole po polu.
 * Kopia dzieli mapę dopłat z szablonem, więc dopłata premierowa dopisana do pierwszego
 * tygodnia "wycieka" do wszystkich kolejnych. Wartości domyślne (sala A, cena z formatu,
 * limit wieku 12) są rozsiane po planerze, kopii i samym seansie.
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
        Screening template = new Screening(request.title(), request.format(), request.hall(),
                request.firstStart(), base, surcharges, !dubbing, dubbing, ageLimit);

        List<Screening> series = new ArrayList<>();
        for (int week = 0; week < request.weeks(); week++) {
            Screening copy = new Screening(template.title(), template.format(),
                    template.hall() != null ? template.hall() : "A",
                    template.start().plusWeeks(week), template.basePrice(), template.surcharges(),
                    template.subtitles(), template.dubbing(), template.ageLimit());
            if (week == 0 && request.premiereWeek()) {
                copy.surcharges().put("PREMIERA", PREMIERE);
            }
            series.add(copy);
        }
        if (request.lastWeekHall() != null) {
            Screening last = series.getLast();
            series.set(series.size() - 1, new Screening(last.title(), last.format(), request.lastWeekHall(),
                    last.start(), last.basePrice(), last.surcharges(), last.subtitles(), last.dubbing(),
                    last.ageLimit()));
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
