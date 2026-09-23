package pl.training.workshop.m4.s09_extractclass.step1;

/**
 * Krok 1 (ZŁY WYNIK pośredni): Extract Class przeniosło tylko DANE klienta.
 * Worek z getterami - cała wiedza o formatowaniu kontaktu nadal siedzi w Booking.
 * Gdyby tu się zatrzymać, mamy o jedną klasę więcej i żadnej nowej odpowiedzialności.
 */
public record Customer(String name, String email, String phone) {
}
