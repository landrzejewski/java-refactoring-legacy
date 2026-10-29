package pl.training.workshop.exercises.e02_hallmap.step3;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 3: węzeł drzewa - bez zmian. */
public abstract class HallGroup implements HallElement {
    private final List<? extends HallElement> children;

    protected HallGroup(List<? extends HallElement> children) {
        this.children = List.copyOf(children);
    }

    @Override
    public final int capacity() {
        int capacity = 0;
        for (HallElement child : children) {
            capacity += child.capacity();
        }
        return capacity;
    }

    @Override
    public final Money surcharges() {
        Money surcharges = Money.ZERO;
        for (HallElement child : children) {
            surcharges = surcharges.plus(child.surcharges());
        }
        return surcharges;
    }

    List<? extends HallElement> children() {
        return children;
    }
}
