package pl.training.workshop.exercises.e09_cashdrawer.step2;

/** Krok 2: wpis przyjmuje odwiedzającego i sam wybiera jego metodę (double dispatch). */
public interface DrawerEntry {
    <R> R accept(DrawerEntryVisitor<R> visitor);
}
