package pl.training.workshop.exercises.e10_showtime.step1

/**
 * Krok 1: urządzenie zgłasza, co się stało; o tym, kto i w jakiej kolejności reaguje, decyduje
 * sterownik sali. Urządzenia znają tylko ten interfejs.
 */
interface HallMediator {
    fun doorsClosed()

    fun doorsBlocked()

    fun doorsCleared()

    fun adsFinished()
}
