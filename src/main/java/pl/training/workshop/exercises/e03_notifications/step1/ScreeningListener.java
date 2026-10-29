package pl.training.workshop.exercises.e03_notifications.step1;

/**
 * Krok 1: wspolny interfejs odbiorcow zmian repertuaru. Scheduler zna tylko ten typ,
 * a nie klasy powiadomien, listy oczekujacych ani statystyk.
 */
public interface ScreeningListener {
    void screeningMoved(ScreeningMoved event);

    void screeningCancelled(ScreeningCancelled event);
}
