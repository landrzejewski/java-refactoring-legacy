package pl.training.workshop.m6.s15_command.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: gałąź SELL jako obiekt komendy. */
public final class SellCommand implements ConsoleCommand {
    @Override
    public String execute(String args, Till till) {
        String[] sell = args.split(" ", 2);
        if (sell.length < 2 || !sell[0].matches("\\d+")) {
            return "Blad: SELL <liczba> <tytul>";
        }
        int quantity = Integer.parseInt(sell[0]);
        Money price = till.priceOf(sell[1]);
        if (price == null) {
            return "Blad: nieznany film " + sell[1];
        }
        Money total = price.times(quantity);
        till.sold(quantity, total);
        return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
    }
}
