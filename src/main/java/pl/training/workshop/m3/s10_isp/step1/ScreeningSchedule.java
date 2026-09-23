package pl.training.workshop.m3.s10_isp.step1;

import java.time.LocalTime;
import java.util.List;

/** Krok 1: rola z perspektywy tablicy seansów. */
public interface ScreeningSchedule {
    void scheduleScreening(String title, LocalTime start);

    void cancelScreening(String title);

    List<String> screenings();
}
