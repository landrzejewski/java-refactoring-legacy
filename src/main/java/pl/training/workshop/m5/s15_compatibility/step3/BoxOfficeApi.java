package pl.training.workshop.m5.s15_compatibility.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: Generalize Parameter Type (Change Signature ⌘F6) - quote przyjmuje każdy Ticket.
 * Zgodne ŹRÓDŁOWO (stary kod po rekompilacji działa), niezgodne BINARNIE: deskryptor
 * quote(LStudentTicket;) zniknął, więc stara wtyczka dostanie NoSuchMethodError.
 */
public final class BoxOfficeApi {
    public Money quote(Ticket ticket) {
        return ticket.price();
    }
}
