package pl.training.workshop.exercises.e02_hallmap.start;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SeatIterator implements Iterator<Seat> {

    private final Deque<Iterator<? extends HallElement>> elements = new ArrayDeque<>();
    private Seat next;

    public SeatIterator(HallGroup group) {
        elements.push(group.children.iterator());
        changeNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Seat next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        var result = next;
        changeNext();
        return result;
    }

    private void changeNext() {
        next = null;
        while (!elements.isEmpty()) {
            var iterator = elements.peek();
            if (!iterator.hasNext()) {
                elements.pop();
                continue;
            }
            var element = iterator.next();
            if (element instanceof HallGroup group) {
                elements.push(group.children.iterator());
            } else {
                next = (Seat) element;
                return;
            }
        }
    }

}
