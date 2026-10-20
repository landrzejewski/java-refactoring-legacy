package pl.training.workshop.m5.s32_mediator.start;

import java.util.ArrayList;
import java.util.List;

/**
 * Start: koszyk (miejsca w kolejności wyboru). Usunięcie i czyszczenie same
 * odświeżają mapę, sumę i przycisk.
 */
public final class Basket {
    private final List<String> seats = new ArrayList<>();
    private SeatMap seatMap;
    private PriceSummary summary;
    private PayButton payButton;

    void connect(SeatMap seatMap, PriceSummary summary, PayButton payButton) {
        this.seatMap = seatMap;
        this.summary = summary;
        this.payButton = payButton;
    }

    /** Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie). */
    public void remove(String seat) {
        if (!seats.remove(seat)) {
            return;
        }
        seatMap.deselect(seat);
        summary.recalculate(seats);
        payButton.refresh(seats.size(), summary.total());
    }

    /** Akcja użytkownika: czyści koszyk. Woła ją też przycisk po płatności. */
    public void clear() {
        seats.clear();
        seatMap.deselectAll();
        summary.recalculate(seats);
        payButton.refresh(seats.size(), summary.total());
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
