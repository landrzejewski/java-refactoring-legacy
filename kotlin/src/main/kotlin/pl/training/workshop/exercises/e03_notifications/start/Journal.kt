package pl.training.workshop.exercises.e03_notifications.start

/**
 * Start: bramki wysylkowe kina (e-mail, SMS, push) i dziennik repertuaru w jednym miejscu.
 * To "urzadzenia" sceny - nie refaktoryzujemy ich, test czyta z nich kolejnosc efektow.
 */
class Journal {
    private val lines = mutableListOf<String>()

    fun email(to: String?, subject: String, body: String) {
        lines += "EMAIL $to | $subject | $body"
    }

    fun sms(number: String?, text: String) {
        lines += "SMS $number | $text"
    }

    fun push(token: String?, title: String, body: String) {
        lines += "PUSH $token | $title | $body"
    }

    fun note(line: String) {
        lines += line
    }

    fun lines(): List<String> = lines.toList()
}
