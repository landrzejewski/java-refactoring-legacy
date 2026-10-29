package pl.training.workshop.exercises.e05_giftcard.start;

import pl.training.workshop.shared.Money;

/**
 * Start: karta podarunkowa ze statusem w polu tekstowym (ISSUED, ACTIVE, EMPTY, BLOCKED, EXPIRED).
 * Każda operacja zaczyna się od switch po statusie, więc tabela przejść jest rozproszona po
 * czterech metodach: nowy status (np. zawieszona) albo nowa operacja to zmiana w każdej z nich,
 * a komunikat "card is blocked" powtarza się w trzech miejscach.
 */
public final class GiftCard {
    private String status = "ISSUED";
    private Money balance;

    public GiftCard(Money initialBalance) {
        if (!initialBalance.isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("initial balance must be positive");
        }
        this.balance = initialBalance;
    }

    public void activate() {
        switch (status) {
            case "ISSUED" -> status = "ACTIVE";
            case "ACTIVE" -> throw new IllegalStateException("card already active");
            case "EMPTY" -> throw new IllegalStateException("card is empty");
            case "BLOCKED" -> throw new IllegalStateException("card is blocked");
            case "EXPIRED" -> throw new IllegalStateException("card has expired");
            default -> throw new IllegalStateException("unknown status: " + status);
        }
    }

    public void charge(Money amount) {
        if (!amount.isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("amount must be positive");
        }
        switch (status) {
            case "ISSUED" -> throw new IllegalStateException("card not activated");
            case "ACTIVE" -> {
                if (amount.isGreaterThan(balance)) {
                    throw new IllegalStateException("insufficient balance");
                }
                balance = balance.minus(amount);
                if (balance.compareTo(Money.ZERO) == 0) {
                    status = "EMPTY";
                }
            }
            case "EMPTY" -> throw new IllegalStateException("card is empty");
            case "BLOCKED" -> throw new IllegalStateException("card is blocked");
            case "EXPIRED" -> throw new IllegalStateException("card has expired");
            default -> throw new IllegalStateException("unknown status: " + status);
        }
    }

    public void block() {
        switch (status) {
            case "ISSUED", "ACTIVE" -> status = "BLOCKED";
            case "EMPTY" -> throw new IllegalStateException("card is empty");
            case "BLOCKED" -> throw new IllegalStateException("card is blocked");
            case "EXPIRED" -> throw new IllegalStateException("card has expired");
            default -> throw new IllegalStateException("unknown status: " + status);
        }
    }

    public void expire() {
        switch (status) {
            case "ISSUED", "ACTIVE", "EMPTY", "BLOCKED" -> status = "EXPIRED";
            case "EXPIRED" -> throw new IllegalStateException("card has expired");
            default -> throw new IllegalStateException("unknown status: " + status);
        }
    }

    public String status() {
        return status;
    }

    public Money balance() {
        return balance;
    }
}
