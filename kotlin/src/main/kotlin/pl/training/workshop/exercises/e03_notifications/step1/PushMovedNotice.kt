package pl.training.workshop.exercises.e03_notifications.step1

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Krok 1: powiadomienie push o przeniesieniu seansu - trzecia kopia tej samej tresci. */
class PushMovedNotice(private val journal: Journal, private val before: Screening, private val after: Screening) {
    fun send(customer: Customer) {
        val subject = "Zmiana seansu: ${after.title}"
        val body = "Seans ${after.title} z ${before.start} przeniesiony na ${after.start}, sala ${after.hall}. " +
            "Bilety zachowuja waznosc."
        journal.push(customer.pushToken, subject, body)
    }
}
