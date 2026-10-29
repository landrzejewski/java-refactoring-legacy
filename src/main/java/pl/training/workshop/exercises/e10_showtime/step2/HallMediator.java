package pl.training.workshop.exercises.e10_showtime.step2;

/**
 * Krok 2: urządzenie zgłasza, co się stało; o tym, kto i w jakiej kolejności reaguje, decyduje
 * sterownik sali. Urządzenia znają tylko ten interfejs.
 */
interface HallMediator {
    void doorsClosed();

    void doorsBlocked();

    void doorsCleared();

    void adsFinished();
}
