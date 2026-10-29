package pl.training.workshop.exercises.e09_cashdrawer.start;

import pl.training.workshop.shared.Money;

/** Start: pogotowie kasowe (wpłata do szuflady) - gotówka, ale bez linii fiskalnej. */
public record Deposit(Money amount, String note) implements DrawerEntry {
}
