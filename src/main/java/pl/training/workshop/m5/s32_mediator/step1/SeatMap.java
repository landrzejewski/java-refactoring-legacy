package pl.training.workshop.m5.s32_mediator.step1;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Krok 1: mapa miejsc zna już tylko mediatora - zgłasza zmianę zaznaczenia i nie wie,
 * kto na nią reaguje. Wciąż udostępnia operacje, które wołają na niej inni (deselect, markSold).
 */
public final class SeatMap {
    private final Set<String> hall = hall();
    private final Set<String> sold = new TreeSet<>(Set.of("B2"));
    private final Set<String> selected = new TreeSet<>();
    private final SelectionMediator mediator;

    public SeatMap(SelectionMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    /** Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane. */
    public void click(String seat) {
        if (!hall.contains(seat) || sold.contains(seat)) {
            return;
        }
        boolean nowSelected = !selected.remove(seat);
        if (nowSelected) {
            selected.add(seat);
        }
        mediator.seatToggled(seat, nowSelected);
    }

    void deselect(String seat) {
        selected.remove(seat);
    }

    void deselectAll() {
        selected.clear();
    }

    void markSold(List<String> seats) {
        sold.addAll(seats);
        seats.forEach(selected::remove);
    }

    String describe() {
        return "wybrane=" + selected + " sprzedane=" + sold;
    }

    private static Set<String> hall() {
        Set<String> seats = new TreeSet<>();
        for (char row = 'A'; row <= 'C'; row++) {
            for (int number = 1; number <= 4; number++) {
                seats.add("" + row + number);
            }
        }
        return seats;
    }
}
