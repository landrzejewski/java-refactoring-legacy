package pl.training.workshop.m5.s32_mediator.start;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Start: mapa miejsc (sala A-C x 1-4, B2 sprzedane wcześniej). Po kliknięciu sama aktualizuje
 * koszyk, podsumowanie i przycisk - zna trzy inne komponenty i kolejność ich odświeżania.
 */
public final class SeatMap {
    private final Set<String> hall = hall();
    private final Set<String> sold = new TreeSet<>(Set.of("B2"));
    private final Set<String> selected = new TreeSet<>();
    private Basket basket;
    private PriceSummary summary;
    private PayButton payButton;

    void connect(Basket basket, PriceSummary summary, PayButton payButton) {
        this.basket = basket;
        this.summary = summary;
        this.payButton = payButton;
    }

    /** Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane. */
    public void click(String seat) {
        if (!hall.contains(seat) || sold.contains(seat)) {
            return;
        }
        if (selected.remove(seat)) {
            basket.drop(seat);
        } else {
            selected.add(seat);
            basket.add(seat);
        }
        summary.recalculate(basket.seats());
        payButton.refresh(basket.seats().size(), summary.total());
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
