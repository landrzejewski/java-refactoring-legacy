package pl.training.workshop.exercises.e03_notifications.step1

/**
 * Krok 1: powiadamianie klientow wyjete ze schedulera do pierwszego odbiorcy zdarzen.
 * Lancuch if wyboru kanalu i szesc klas powiadomien przeniosly sie tu bez zmian - to temat kolejnych krokow.
 */
class CustomerNotifier(private val journal: Journal) : ScreeningListener {
    override fun screeningMoved(event: ScreeningMoved) {
        for (customer in event.ticketHolders) {
            if (!customer.pushToken.isNullOrBlank() && !customer.pushToken.startsWith("expired-")) {
                PushMovedNotice(journal, event.before, event.after).send(customer)
            } else if (!customer.phone.isNullOrBlank()) {
                SmsMovedNotice(journal, event.before, event.after).send(customer)
            } else if (!customer.email.isNullOrBlank()) {
                EmailMovedNotice(journal, event.before, event.after).send(customer)
            } else {
                journal.note("UNDELIVERED ${customer.name} (brak kanalu)")
            }
        }
    }

    override fun screeningCancelled(event: ScreeningCancelled) {
        for (customer in event.ticketHolders) {
            if (!customer.pushToken.isNullOrBlank() && !customer.pushToken.startsWith("expired-")) {
                PushCancelledNotice(journal, event.screening, event.reason).send(customer)
            } else if (!customer.phone.isNullOrBlank()) {
                SmsCancelledNotice(journal, event.screening, event.reason).send(customer)
            } else if (!customer.email.isNullOrBlank()) {
                EmailCancelledNotice(journal, event.screening, event.reason).send(customer)
            } else {
                journal.note("UNDELIVERED ${customer.name} (brak kanalu)")
            }
        }
    }
}
