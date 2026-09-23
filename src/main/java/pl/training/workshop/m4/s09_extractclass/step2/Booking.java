package pl.training.workshop.m4.s09_extractclass.step2;

import java.math.BigDecimal;

/**
 * Krok 2: {@code contact()} deleguje do {@link Customer#contactLine()} - zostaje jako fasada,
 * bo jest częścią publicznego API rezerwacji. Płatność nadal wymieszana z rezerwacją.
 */
public final class Booking {
    private final String id;
    private final Customer customer;
    private final BigDecimal amount;
    private String cardNumber;
    private String paymentStatus = "NEW";

    public Booking(String id, String customerName, String customerEmail, String customerPhone,
                   BigDecimal amount) {
        this.id = id;
        this.customer = new Customer(customerName, customerEmail, customerPhone);
        this.amount = amount;
    }

    public String contact() {
        return customer.contactLine();
    }

    public void pay(String card) {
        if (!paymentStatus.equals("NEW")) {
            throw new IllegalStateException("Rezerwacja " + id + " jest juz oplacona");
        }
        cardNumber = card.replace(" ", "");
        paymentStatus = "PAID";
    }

    public boolean isPaid() {
        return paymentStatus.equals("PAID");
    }

    public String summary() {
        return "Rezerwacja " + id + "\n"
                + "Klient: " + contact() + "\n"
                + "Kwota: " + amount + "\n"
                + "Platnosc: " + (isPaid() ? "oplacona karta " + maskedCard() : "oczekuje") + "\n";
    }

    private String maskedCard() {
        return "**** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
