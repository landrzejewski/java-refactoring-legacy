package pl.training.workshop.m5.s32_mediator.start;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Start: przycisk płatności - aktywny dla 1-4 miejsc. Po naciśnięciu sam pyta koszyk i sumę,
 * oznacza miejsca na mapie jako sprzedane i czyści koszyk, co uruchamia kaskadę odświeżeń.
 */
public final class PayButton {
    private static final int MAX_SEATS = 4;

    private boolean enabled;
    private String label = "Wybierz miejsca";
    private SeatMap seatMap;
    private Basket basket;
    private PriceSummary summary;

    void connect(SeatMap seatMap, Basket basket, PriceSummary summary) {
        this.seatMap = seatMap;
        this.basket = basket;
        this.summary = summary;
    }

    /** Akcja użytkownika: płatność za zawartość koszyka. */
    public String press() {
        if (!enabled) {
            return "Nieaktywny: " + label;
        }
        List<String> paid = basket.seats();
        Money amount = summary.total();
        seatMap.markSold(paid);
        basket.clear();
        return "Zaplacono " + amount + " za " + String.join(",", paid);
    }

    void refresh(int seats, Money total) {
        enabled = seats > 0 && seats <= MAX_SEATS;
        if (seats == 0) {
            label = "Wybierz miejsca";
        } else if (seats > MAX_SEATS) {
            label = "Maksymalnie " + MAX_SEATS + " miejsca";
        } else {
            label = "Zaplac " + total;
        }
    }

    String describe() {
        return "przycisk=" + label;
    }
}
