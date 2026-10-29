package pl.training.workshop.exercises.e09_cashdrawer.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: sprzedaż biletu za gotówkę - zasila szufladę. */
public record CashSale(String reference, Money amount) implements DrawerEntry {
    @Override
    public <R> R accept(DrawerEntryVisitor<R> visitor) {
        return visitor.visitCashSale(this);
    }
}
