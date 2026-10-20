package pl.training.workshop.m5.s32_mediator.step2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Krok 2: koszyk zna tylko mediatora - zgłasza usunięcie i wyczyszczenie, nie odświeża innych. */
public final class Basket {
    private final List<String> seats = new ArrayList<>();
    private final SelectionMediator mediator;

    public Basket(SelectionMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    /** Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie). */
    public void remove(String seat) {
        if (!seats.remove(seat)) {
            return;
        }
        mediator.seatRemoved(seat);
    }

    /** Akcja użytkownika: czyści koszyk. Woła ją też mediator po płatności. */
    public void clear() {
        seats.clear();
        mediator.basketCleared();
    }

    void add(String seat) {
        seats.add(seat);
    }

    void drop(String seat) {
        seats.remove(seat);
    }

    List<String> seats() {
        return List.copyOf(seats);
    }

    String describe() {
        return "koszyk=" + seats;
    }
}
