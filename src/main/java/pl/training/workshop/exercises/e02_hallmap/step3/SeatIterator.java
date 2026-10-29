package pl.training.workshop.exercises.e02_hallmap.step3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Krok 3: przejście po miejscach w głąb, od lewej - bez zmian. */
final class SeatIterator implements Iterator<Seat> {
    private final Deque<Iterator<? extends HallElement>> path = new ArrayDeque<>();
    private Seat next;

    SeatIterator(HallGroup root) {
        path.push(root.children().iterator());
        advance();
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
        Seat current = next;
        advance();
        return current;
    }

    private void advance() {
        next = null;
        while (!path.isEmpty()) {
            Iterator<? extends HallElement> level = path.peek();
            if (!level.hasNext()) {
                path.pop();
                continue;
            }
            HallElement element = level.next();
            if (element instanceof HallGroup group) {
                path.push(group.children().iterator());
            } else {
                next = (Seat) element;
                return;
            }
        }
    }
}
