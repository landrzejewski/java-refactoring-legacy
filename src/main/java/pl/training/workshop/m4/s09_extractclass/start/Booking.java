package pl.training.workshop.m4.s09_extractclass.start;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Start: rezerwacja, która wie wszystko - o kliencie (imię, e-mail, telefon i ich formatowanie)
 * i o płatności (karta, status, maskowanie). Trzy powody zmiany w jednej klasie.
 */
public final class Booking {
    private final String id;
    private final String customerName;
    private final String customerEmail;
    private final String customerPhone;
    private final BigDecimal amount;
    private String cardNumber;
    private String paymentStatus = "NEW";

    public Booking(String id, String customerName, String customerEmail, String customerPhone,
                   BigDecimal amount) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.amount = amount;
    }

    public String contact() {
        return customerName + " <" + customerEmail.trim().toLowerCase(Locale.ROOT) + ">, tel. "
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
        String digits = customerPhone.replaceAll("\\D", "");
        String local = digits.substring(digits.length() - 9);
        return local.substring(0, 3) + "-" + local.substring(3, 6) + "-" + local.substring(6);
    }

    private String maskedCard() {
        return "**** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
