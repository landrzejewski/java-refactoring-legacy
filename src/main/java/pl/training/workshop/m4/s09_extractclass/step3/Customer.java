package pl.training.workshop.m4.s09_extractclass.step3;

import java.util.Locale;

/**
 * Krok 2 (bez zmian w kroku 3): Move Method {@code contact} i {@code formattedPhone} do Customer.
 * Klient sam wie, jak się go przedstawia - dane i zachowanie mają jednego właściciela.
 */
public record Customer(String name, String email, String phone) {
    public String contactLine() {
        return name + " <" + email.trim().toLowerCase(Locale.ROOT) + ">, tel. " + formattedPhone();
    }

    private String formattedPhone() {
        String digits = phone.replaceAll("\\D", "");
        String local = digits.substring(digits.length() - 9);
        return local.substring(0, 3) + "-" + local.substring(3, 6) + "-" + local.substring(6);
    }
}
