package pl.training.workshop.m4.s09_extractclass.step1;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Krok 1: pola klienta przeniesione do {@link Customer} (Move Field x3). Logika nadal tutaj
 * i sięga po dane przez gettery: {@code customer.email()}, {@code customer.phone()}.
 * Konstruktor i publiczne API bez zmian - klienci Booking nic nie zauważyli.
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
        return customer.name() + " <" + customer.email().trim().toLowerCase(Locale.ROOT) + ">, tel. "
                + formattedPhone();
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

    private String formattedPhone() {
        String digits = customer.phone().replaceAll("\\D", "");
        String local = digits.substring(digits.length() - 9);
        return local.substring(0, 3) + "-" + local.substring(3, 6) + "-" + local.substring(6);
    }

    private String maskedCard() {
        return "**** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
