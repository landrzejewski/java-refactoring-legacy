package pl.training.workshop.exercises.e03_notifications;

/**
 * Stabilny kontrakt sceny: klient z biletem albo z listy oczekujacych. Kazdy kanal kontaktu
 * moze byc {@code null} albo pusty; token push zaczynajacy sie od {@code expired-} jest niewazny.
 */
public record Customer(String name, String email, String phone, String pushToken) {
}
