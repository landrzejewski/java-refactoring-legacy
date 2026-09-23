package pl.training.workshop.m8.s11_stagedrollout.start;

/**
 * Start: wdrożenie nowego procesu płatności "na flagę". Stała w kodzie (zmiana = nowe wydanie),
 * lista testerów wpisana w if, brak podziału na etapy i brak wyłącznika awaryjnego.
 */
public final class CheckoutRouter {
    static final boolean NEW_CHECKOUT = false;

    public boolean useNewCheckout(String email) {
        if (NEW_CHECKOUT) {
            return true;
        }
        return email.equals("anna@kino.pl") || email.equals("jan@kino.pl");
    }
}
