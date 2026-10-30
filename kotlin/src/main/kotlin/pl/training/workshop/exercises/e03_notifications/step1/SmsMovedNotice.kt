package pl.training.workshop.exercises.e03_notifications.step1

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/**
 * Krok 1: SMS o przeniesieniu seansu. Tresc skopiowana z EmailMovedNotice, skracanie do 70 znakow
 * skopiowane z SmsCancelledNotice - zmiana limitu SMS wymaga poprawki w dwoch klasach.
 */
class SmsMovedNotice(private val journal: Journal, private val before: Screening, private val after: Screening) {
    fun send(customer: Customer) {
        val subject = "Zmiana seansu: ${after.title}"
        val body = "Seans ${after.title} z ${before.start} przeniesiony na ${after.start}, sala ${after.hall}. " +
            "Bilety zachowuja waznosc."
        var text = "$subject. $body"
        if (text.length > 70) {
            text = text.substring(0, 67) + "..."
        }
        journal.sms(customer.phone, text)
    }
}
