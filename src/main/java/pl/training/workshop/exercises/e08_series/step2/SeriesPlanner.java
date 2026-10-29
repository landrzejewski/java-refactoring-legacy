package pl.training.workshop.exercises.e08_series.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e08_series.SeriesRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: planer składa szablon budowniczym i ustawia tylko to, co przyszło w zamówieniu.
 * Nie zna już cen formatów, domyślnej sali ani limitu wieku; sprawdza jedynie liczbę tygodni,
 * bo to reguła serii, a nie pojedynczego seansu. Kolejne tygodnie nadal powstają z kopii szablonu.
 */
public final class SeriesPlanner {
    private static final Money PREMIERE = Money.of("5.00");

    public List<Screening> plan(SeriesRequest request) {
        if (request.weeks() < 1) {
            throw new IllegalArgumentException("weeks must be positive");
        }
        Screening.Builder builder = Screening.builder()
                .title(request.title())
                .format(request.format())
                .start(request.firstStart());
        if (request.hall() != null) {
            builder.hall(request.hall());
        }
        if (request.basePrice() != null) {
            builder.basePrice(request.basePrice());
        }
        if (request.dubbing() != null) {
            builder.dubbing(request.dubbing());
        }
        if (request.ageLimit() != null) {
            builder.ageLimit(request.ageLimit());
        }
        Screening template = builder.build();

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
}
