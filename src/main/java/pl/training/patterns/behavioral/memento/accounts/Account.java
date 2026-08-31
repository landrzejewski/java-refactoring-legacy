package pl.training.patterns.behavioral.memento.accounts;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private final UUID number;
    private BigDecimal balance = BigDecimal.ZERO;

    public Account(final UUID number) {
        this.number = Objects.requireNonNull(number);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public Memento createMemento() {
        return new Memento(balance);
    }

    public void restoreMemento(Memento memento) {
        balance = memento.balance();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account(number=" + number + ", balance=" + balance + ")";
    }

}
