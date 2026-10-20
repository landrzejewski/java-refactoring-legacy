package pl.training.workshop.m5.s26_variability.step2;

import pl.training.workshop.m5.s26_variability.Status;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: CANCEL jako polecenie. Ile oddać, decyduje odbiorca (to zależy od statusu);
 * polecenie tylko zapamiętuje wynik, żeby cofnięcie wpłaciło go z powrotem.
 */
public final class CancelCommand implements DeskCommand {
    private final String id;
    private Status previous;
    private Money refunded;

    private CancelCommand(String id) {
        this.id = id;
    }

    static CancelCommand from(String[] args) {
        return new CancelCommand(DeskCommand.idFrom(args, "CANCEL"));
    }

    @Override
    public String execute(BookingLedger ledger) {
        previous = ledger.status(id);
        refunded = ledger.cancel(id);
        return id + " CANCELLED" + (refunded.isGreaterThan(Money.ZERO) ? " zwrot " + refunded : "");
    }

    @Override
    public void undo(BookingLedger ledger) {
        ledger.deposit(refunded);
        ledger.restore(id, previous);
    }

    @Override
    public String journalEntry() {
        return "CANCEL " + id;
    }
}
