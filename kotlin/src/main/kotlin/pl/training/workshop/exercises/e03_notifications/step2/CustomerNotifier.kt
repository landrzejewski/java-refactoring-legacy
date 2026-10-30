package pl.training.workshop.exercises.e03_notifications.step2

/**
 * Krok 2: powiadamianie nie wybiera juz kanalu - dostaje poczatek lancucha dostarczania jako zwykly
 * kanal. Nie ma tu zadnego if po danych kontaktowych ani wpisu o niedostarczeniu.
 * Tresc kazdego rodzaju wiadomosci jest raz, a sposob wysylki (np. skracanie SMS) zostaje po stronie kanalu.
 */
class CustomerNotifier(private val delivery: Channel) : ScreeningListener {
    override fun screeningMoved(event: ScreeningMoved) {
        val subject = "Zmiana seansu: ${event.after.title}"
        val body = "Seans ${event.after.title} z ${event.before.start} przeniesiony na ${event.after.start}, " +
            "sala ${event.after.hall}. Bilety zachowuja waznosc."
        for (customer in event.ticketHolders) {
            delivery.deliver(customer, subject, body)
        }
    }

    override fun screeningCancelled(event: ScreeningCancelled) {
        val subject = "Odwolany seans: ${event.screening.title}"
        val body = "Seans ${event.screening.title} z ${event.screening.start} odwolany (${event.reason}). " +
            "Zwrot w ciagu 7 dni."
        for (customer in event.ticketHolders) {
            delivery.deliver(customer, subject, body)
        }
    }
}
