package pl.training.workshop.exercises.e02_hallmap.start;

import pl.training.workshop.shared.Money;

import java.util.List;

public abstract class HallGroup implements HallElement {

    protected final List<? extends HallElement> children;

    protected HallGroup(List<? extends HallElement> children) {
        this.children = children;
    }

    @Override
    public int capacity() {
        int capacity = 0;
        for (HallElement child : children) {
            capacity += child.capacity();
        }
        return capacity;
    }

    @Override
    public Money surcharges() {
        Money surcharges = Money.ZERO;
        for (HallElement child : children) {
            surcharges = surcharges.plus(child.surcharges());
        }
        return surcharges;
    }

}
