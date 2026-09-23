package pl.training.workshop.m7.s13_godclass.step3;

import java.util.ArrayList;
import java.util.List;

/** Bramka płatności starego systemu. Karty kończące się na 0000 są odrzucane. */
public final class LegacyPaymentGateway {
    public static final List<String> CHARGES = new ArrayList<>();

    private LegacyPaymentGateway() {
    }

    public static boolean charge(String cardNumber, double amount) {
        if (cardNumber == null || cardNumber.endsWith("0000")) {
            CHARGES.add("DECLINED " + cardNumber + " " + String.format(java.util.Locale.ROOT, "%.2f", amount));
            return false;
        }
        CHARGES.add("CHARGED " + cardNumber + " " + String.format(java.util.Locale.ROOT, "%.2f", amount));
        return true;
    }

    public static void refund(String cardNumber, double amount) {
        CHARGES.add("REFUND " + cardNumber + " " + String.format(java.util.Locale.ROOT, "%.2f", amount));
    }
}
