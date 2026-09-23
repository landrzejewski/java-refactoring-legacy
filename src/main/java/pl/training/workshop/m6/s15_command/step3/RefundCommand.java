package pl.training.workshop.m6.s15_command.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: gałąź REFUND jako obiekt komendy. */
public final class RefundCommand implements ConsoleCommand {
    @Override
    public String execute(String args, Till till) {
        Money price = till.priceOf(args);
        if (price == null) {
            return "Blad: nieznany film " + args;
        }
        if (till.tickets() == 0) {
            return "Blad: brak biletow do zwrotu";
        }
        till.refunded(price);
        return "Zwrot 1 x " + args + " = " + price;
    }
}
