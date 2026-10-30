package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Start: e-mail o przeniesieniu seansu. Tresc jest taka sama jak w SmsMovedNotice i PushMovedNotice. */
class EmailMovedNotice(private val journal: Journal, private val before: Screening, private val after: Screening) {
    fun send(customer: Customer) {
        val subject = "Zmiana seansu: ${after.title}"
        val body = "Seans ${after.title} z ${before.start} przeniesiony na ${after.start}, sala ${after.hall}. " +
            "Bilety zachowuja waznosc."
        journal.email(customer.email, subject, body)
    }
}
