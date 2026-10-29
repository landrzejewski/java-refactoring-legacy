package pl.training.workshop.exercises.e09_cashdrawer.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: zwrot gotówki za bilet - pomniejsza szufladę, w eksporcie fiskalnym kwota ujemna. */
public record Refund(String reference, Money amount, String reason) implements DrawerEntry {
    @Override
    public <R> R accept(DrawerEntryVisitor<R> visitor) {
        return visitor.visitRefund(this);
    }
}
