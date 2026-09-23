package pl.training.workshop.m4.s09_extractclass.step3;

import java.math.BigDecimal;

/**
 * Krok 3 (rozwiązanie): Booking składa rezerwację z {@link Customer} i {@link Payment}.
 * Publiczne API (konstruktor, contact, pay, isPaid, summary) bez zmian - to fasada.
 */
public final class Booking {
    private final String id;
    private final Customer customer;
    private final BigDecimal amount;
    private final Payment payment = new Payment();

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
        payment.payWith(card, id);
    }

    public boolean isPaid() {
        return payment.isPaid();
    }

    public String summary() {
        return "Rezerwacja " + id + "\n"
                + "Klient: " + contact() + "\n"
                + "Kwota: " + amount + "\n"
                + "Platnosc: " + payment.description() + "\n";
    }
}
