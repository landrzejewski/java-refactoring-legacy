package pl.training.workshop.exercises.e03_notifications

/**
 * Stabilny kontrakt sceny: klient z biletem albo z listy oczekujacych. Kazdy kanal kontaktu
 * moze byc `null` albo pusty; token push zaczynajacy sie od `expired-` jest niewazny.
 */
data class Customer(val name: String, val email: String?, val phone: String?, val pushToken: String?)
