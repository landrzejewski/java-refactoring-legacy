package pl.training.workshop.exercises.e09_cashdrawer.start;

import pl.training.workshop.shared.Money;

/** Start: zwrot gotówki za bilet - pomniejsza szufladę, w eksporcie fiskalnym kwota ujemna. */
public record Refund(String reference, Money amount, String reason) implements DrawerEntry {
}
