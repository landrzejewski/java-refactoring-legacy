package pl.training.workshop.m5.s15_compatibility.step4;

import pl.training.workshop.shared.Money;

/**
 * Krok 4 (rozwiązanie): stara sygnatura wraca jako przestarzałe przeciążenie delegujące.
 * Stare binaria znajdują swój deskryptor, nowi klienci widzą ostrzeżenie deprecation.
 * Usunięcie planujemy jako osobną, zapowiedzianą zmianę łamiącą (np. w wersji 3.0).
 */
public final class BoxOfficeApi {
    public Money quote(Ticket ticket) {
        return ticket.price();
    }

    /** Zgodność binarna ze skompilowanymi wtyczkami 1.x. */
    @Deprecated(since = "2.0")
    public Money quote(StudentTicket ticket) {
        return quote((Ticket) ticket);
    }
}
