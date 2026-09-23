package pl.training.workshop.m5.s04_extractsuperclass.step3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Krok 3: jedna pętla po wspólnym typie; publiczna sygnatura conflicts(...) bez zmian. */
public final class HallPlanner {
    public List<String> conflicts(List<Screening> screenings, List<PrivateEvent> events) {
        List<HallBooking> bookings = new ArrayList<>(screenings);
        bookings.addAll(events);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < bookings.size(); i++) {
            for (int j = i + 1; j < bookings.size(); j++) {
                HallBooking a = bookings.get(i);
                HallBooking b = bookings.get(j);
                if (a.overlaps(b)) {
                    result.add(a.name() + " x " + b.name());
                }
            }
        }
        Collections.sort(result);
        return result;
    }
}
