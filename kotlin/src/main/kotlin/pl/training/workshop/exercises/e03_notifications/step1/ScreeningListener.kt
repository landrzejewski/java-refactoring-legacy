package pl.training.workshop.exercises.e03_notifications.step1

/**
 * Krok 1: wspolny interfejs odbiorcow zmian repertuaru. Scheduler zna tylko ten typ,
 * a nie klasy powiadomien, listy oczekujacych ani statystyk.
 */
interface ScreeningListener {
    fun screeningMoved(event: ScreeningMoved)

    fun screeningCancelled(event: ScreeningCancelled)
}
