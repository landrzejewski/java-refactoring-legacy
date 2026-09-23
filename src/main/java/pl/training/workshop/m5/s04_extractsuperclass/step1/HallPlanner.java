package pl.training.workshop.m5.s04_extractsuperclass.step1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Krok 1: bez zmian - klient jeszcze nie korzysta z nowego typu. */
public final class HallPlanner {
    public List<String> conflicts(List<Screening> screenings, List<PrivateEvent> events) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < screenings.size(); i++) {
            for (int j = i + 1; j < screenings.size(); j++) {
                Screening a = screenings.get(i);
                Screening b = screenings.get(j);
                if (a.hall().equals(b.hall())
                        && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
                    result.add(a.title() + " x " + b.title());
                }
            }
        }
        for (Screening a : screenings) {
            for (PrivateEvent b : events) {
                if (a.hall().equals(b.hall())
                        && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
                    result.add(a.title() + " x Wynajem: " + b.client());
                }
            }
        }
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                PrivateEvent a = events.get(i);
                PrivateEvent b = events.get(j);
                if (a.hall().equals(b.hall())
                        && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
                    result.add("Wynajem: " + a.client() + " x Wynajem: " + b.client());
                }
            }
        }
        Collections.sort(result);
        return result;
    }
}
