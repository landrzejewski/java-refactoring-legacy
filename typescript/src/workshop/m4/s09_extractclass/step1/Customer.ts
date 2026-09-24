/**
 * Krok 1 (ZŁY WYNIK pośredni): Extract Class przeniosło tylko DANE klienta.
 * Worek z polami - cała wiedza o formatowaniu kontaktu nadal siedzi w Booking.
 * Gdyby tu się zatrzymać, mamy o jedną klasę więcej i żadnej nowej odpowiedzialności.
 */
export class Customer {
  constructor(readonly name: string, readonly email: string, readonly phone: string) {}
}
