package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/**
 * Krok 2: pierwsze ogniwo dostarczania. Obsluguje klienta z waznym tokenem push, resztę oddaje nastepnikowi.
 * Ogniwo nie wie, co jest dalej - kolejnosc sklada korzen kompozycji.
 */
class PushLink(private val push: PushChannel, private val next: Channel) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        val token = customer.pushToken
        if (!token.isNullOrBlank() && !token.startsWith("expired-")) {
            push.deliver(customer, subject, body)
        } else {
            next.deliver(customer, subject, body)
        }
    }
}
