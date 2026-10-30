package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/** Krok 2: kanal SMS - sklejenie tytulu z trescia i limit 70 znakow sa teraz w jednym miejscu. */
class SmsChannel(private val journal: Journal) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        var text = "$subject. $body"
        if (text.length > MAX_LENGTH) {
            text = text.substring(0, MAX_LENGTH - 3) + "..."
        }
        journal.sms(customer.phone, text)
    }

    private companion object {
        const val MAX_LENGTH = 70
    }
}
