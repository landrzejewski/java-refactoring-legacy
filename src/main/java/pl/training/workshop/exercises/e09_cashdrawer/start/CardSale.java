package pl.training.workshop.exercises.e09_cashdrawer.start;

import pl.training.workshop.shared.Money;

/** Start: sprzedaż biletu kartą - nie dotyka gotówki, ale trafia do eksportu fiskalnego. */
public record CardSale(String reference, Money amount, String cardLast4) implements DrawerEntry {
}
