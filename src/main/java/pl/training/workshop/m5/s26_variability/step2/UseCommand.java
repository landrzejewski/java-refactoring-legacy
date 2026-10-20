package pl.training.workshop.m5.s26_variability.step2;

import pl.training.workshop.m5.s26_variability.Status;

/** Krok 2: USE jako polecenie - cofnięcie przywraca tylko poprzedni status. */
public final class UseCommand implements DeskCommand {
    private final String id;
    private Status previous;

    private UseCommand(String id) {
        this.id = id;
    }

    static UseCommand from(String[] args) {
        return new UseCommand(DeskCommand.idFrom(args, "USE"));
    }

    @Override
    public String execute(BookingLedger ledger) {
        previous = ledger.status(id);
        ledger.use(id);
        return id + " USED";
    }

    @Override
    public void undo(BookingLedger ledger) {
        ledger.restore(id, previous);
    }

    @Override
    public String journalEntry() {
        return "USE " + id;
    }
}
