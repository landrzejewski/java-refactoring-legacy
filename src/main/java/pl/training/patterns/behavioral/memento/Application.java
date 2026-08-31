package pl.training.patterns.behavioral.memento;

import pl.training.patterns.behavioral.memento.accounts.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Application {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        var account = new Account(UUID.randomUUID());
        var caretaker = List.of(account.createMemento());
        account.deposit(BigDecimal.TEN);
        log.info("Deposited balance: " + account.getBalance());
        account.restoreMemento(caretaker.getFirst());
        log.info("Restored balance: " + account.getBalance());
    }
}
