package pl.training.workshop.exercises.e09_cashdrawer.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: sprzedaż biletu kartą - nie dotyka gotówki, ale trafia do eksportu fiskalnego. */
public record CardSale(String reference, Money amount, String cardLast4) implements DrawerEntry {
    @Override
    public <R> R accept(DrawerEntryVisitor<R> visitor) {
        return visitor.visitCardSale(this);
    }
}
