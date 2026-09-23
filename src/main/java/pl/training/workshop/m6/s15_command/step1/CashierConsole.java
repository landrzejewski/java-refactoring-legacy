package pl.training.workshop.m6.s15_command.step1;

import java.util.Locale;
import java.util.Map;

import pl.training.workshop.shared.Money;

/** Krok 1: Extract Method - ciało każdej gałęzi w osobnej metodzie o tej samej sygnaturze. */
public final class CashierConsole {
    private static final Map<String, Money> PRICES = Map.of(
            "Diuna", Money.of("40.00"),
            "Kraina Lodu", Money.of("32.00"),
            "Amator", Money.of("25.00"));

    private Money cash = Money.ZERO;
    private int tickets;

    public String handle(String line) {
        String[] parts = line.strip().split(" ", 2);
        String command = parts[0].toUpperCase(Locale.ROOT);
        String args = parts.length > 1 ? parts[1] : "";
        if (command.equals("SELL")) {
            return sell(args);
        } else if (command.equals("REFUND")) {
            return refund(args);
        } else if (command.equals("REPORT")) {
            return report(args);
        }
        return "Nieznana komenda: " + parts[0];
    }

    private String sell(String args) {
        String[] sell = args.split(" ", 2);
        if (sell.length < 2 || !sell[0].matches("\\d+")) {
            return "Blad: SELL <liczba> <tytul>";
        }
        int quantity = Integer.parseInt(sell[0]);
        Money price = PRICES.get(sell[1]);
        if (price == null) {
            return "Blad: nieznany film " + sell[1];
        }
        Money total = price.times(quantity);
        cash = cash.plus(total);
        tickets += quantity;
        return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
    }

    private String refund(String args) {
        Money price = PRICES.get(args);
        if (price == null) {
            return "Blad: nieznany film " + args;
        }
        if (tickets == 0) {
            return "Blad: brak biletow do zwrotu";
        }
        cash = cash.minus(price);
        tickets--;
        return "Zwrot 1 x " + args + " = " + price;
    }

    private String report(String args) {
        return "Kasa: " + cash + ", biletow: " + tickets;
    }
}
