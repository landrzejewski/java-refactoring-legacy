/**
 * Start: wdrożenie nowego procesu płatności "na flagę". Stała w kodzie (zmiana = nowe wydanie),
 * lista testerów wpisana w if, brak podziału na etapy i brak wyłącznika awaryjnego.
 */
export class CheckoutRouter {
  static readonly NEW_CHECKOUT: boolean = false;

  useNewCheckout(email: string): boolean {
    if (CheckoutRouter.NEW_CHECKOUT) {
      return true;
    }
    return email === 'anna@kino.pl' || email === 'jan@kino.pl';
  }
}
