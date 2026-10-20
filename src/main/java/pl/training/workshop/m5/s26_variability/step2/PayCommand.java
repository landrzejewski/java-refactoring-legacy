package pl.training.workshop.m5.s26_variability.step2;

import pl.training.workshop.m5.s26_variability.Status;
import pl.training.workshop.shared.Money;

/** Krok 2: PAY jako polecenie - pamięta poprzedni status i pobraną kwotę, żeby je odwrócić. */
public final class PayCommand implements DeskCommand {
    private final String id;
    private Status previous;
    private Money paid;

    private PayCommand(String id) {
        this.id = id;
    }

    static PayCommand from(String[] args) {
        return new PayCommand(DeskCommand.idFrom(args, "PAY"));
    }

    @Override
    public String execute(BookingLedger ledger) {
        previous = ledger.status(id);
        paid = ledger.pay(id);
        return id + " PAID " + paid;
    }

    @Override
    public void undo(BookingLedger ledger) {
        ledger.withdraw(paid);
        ledger.restore(id, previous);
    }

    @Override
    public String journalEntry() {
        return "PAY " + id;
    }
}
