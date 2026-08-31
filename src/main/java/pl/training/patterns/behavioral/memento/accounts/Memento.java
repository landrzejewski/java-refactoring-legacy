package pl.training.patterns.behavioral.memento.accounts;

import java.math.BigDecimal;

public class Memento {

    private final BigDecimal balance;

    Memento(final BigDecimal balance) {
        this.balance = balance;
    }

    BigDecimal balance() {
        return balance;
    }

}
