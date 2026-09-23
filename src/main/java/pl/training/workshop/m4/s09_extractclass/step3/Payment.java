package pl.training.workshop.m4.s09_extractclass.step3;

/**
 * Krok 3: Extract Class - płatność. Własny stan (karta, status), własne reguły
 * (jedna płatność, maskowanie karty) i własny powód zmiany (bramka płatności, PCI).
 * Nie zna Booking: id dostaje jako wartość, więc nie ma referencji zwrotnej ani cyklu.
 */
public final class Payment {
    private String cardNumber;
    private String status = "NEW";

    public void payWith(String card, String bookingId) {
        if (!status.equals("NEW")) {
            throw new IllegalStateException("Rezerwacja " + bookingId + " jest juz oplacona");
        }
        cardNumber = card.replace(" ", "");
        status = "PAID";
    }

    public boolean isPaid() {
        return status.equals("PAID");
    }

    public String description() {
        return isPaid() ? "oplacona karta " + maskedCard() : "oczekuje";
    }

    private String maskedCard() {
        return "**** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
