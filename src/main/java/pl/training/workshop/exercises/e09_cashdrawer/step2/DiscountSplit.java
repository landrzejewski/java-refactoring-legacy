package pl.training.workshop.exercises.e09_cashdrawer.step2;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: kontrakt podziału rabatu grupowego na bilety. Wynik ma tyle pozycji, ile biletów,
 * a jego suma to dokładnie kwota rabatu - zaokrąglenie jest sprawą każdego wariantu.
 */
@FunctionalInterface
public interface DiscountSplit {
    List<Money> split(Money discount, List<Money> tickets);
}
