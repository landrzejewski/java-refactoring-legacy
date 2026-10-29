package pl.training.workshop.exercises.e09_cashdrawer.start;

import pl.training.workshop.shared.Money;

/** Start: sprzedaż biletu za gotówkę - zasila szufladę. */
public record CashSale(String reference, Money amount) implements DrawerEntry {
}
