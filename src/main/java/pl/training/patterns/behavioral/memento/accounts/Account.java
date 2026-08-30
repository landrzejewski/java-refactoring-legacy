package pl.training.patterns.behavioral.memento.accounts;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    UUID number;
    BigDecimal balance = BigDecimal.ZERO;

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    @Override
    public String toString() {
        return "Account(number=" + this.number + ", balance=" + this.getBalance() + ")";
    }

    public Account(final UUID number) {
        if (number == null) {
            throw new NullPointerException("number is marked non-null but is null");
        }
        this.number = number;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }
}
