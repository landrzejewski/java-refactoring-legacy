package pl.training.workshop.exercises.e09_cashdrawer.step1;

/** Krok 1: wpis przyjmuje odwiedzającego i sam wybiera jego metodę (double dispatch). */
public interface DrawerEntry {
    <R> R accept(DrawerEntryVisitor<R> visitor);
}
