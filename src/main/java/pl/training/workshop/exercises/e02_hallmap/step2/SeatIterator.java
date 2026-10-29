package pl.training.workshop.exercises.e02_hallmap.step2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Krok 2: przejście po wszystkich miejscach sali w kolejności sektorów i rzędów (w głąb,
 * od lewej). Klient dostaje kolejne miejsca i nie wie, ile poziomów ma drzewo ani że luki
 * w ogóle istnieją. Wersja funkcyjna tego samego: {@code flatMap} po dzieciach.
 */
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
