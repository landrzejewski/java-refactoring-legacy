package pl.training.workshop.exercises.e09_cashdrawer.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: pogotowie kasowe (wpłata do szuflady) - gotówka, ale bez linii fiskalnej. */
public record Deposit(Money amount, String note) implements DrawerEntry {
    @Override
    public <R> R accept(DrawerEntryVisitor<R> visitor) {
        return visitor.visitDeposit(this);
    }
}
