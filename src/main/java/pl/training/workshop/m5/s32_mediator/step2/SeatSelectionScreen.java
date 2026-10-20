package pl.training.workshop.m5.s32_mediator.step2;

import java.util.List;

/**
 * Stabilny punkt wejścia sceny. Krok 2: ekran-mediator obsługuje wszystkie zdarzenia, a kolejność
 * odświeżania (suma, potem przycisk) jest w jednej metodzie refresh() zamiast w czterech kopiach.
 * Zostało jedno połączenie wprost: przycisk czyta koszyk i sumę.
 */
public final class SeatSelectionScreen implements SelectionMediator {
    private final SeatMap seatMap = new SeatMap(this);
    private final Basket basket = new Basket(this);
    private final PriceSummary summary = new PriceSummary(this);
    private final PayButton payButton = new PayButton(this);

    public SeatSelectionScreen() {
        payButton.connect(basket, summary);
    }

    @Override
    public void seatToggled(String seat, boolean selected) {
        if (selected) {
            basket.add(seat);
        } else {
            basket.drop(seat);
        }
        refresh();
    }

    @Override
    public void seatRemoved(String seat) {
        seatMap.deselect(seat);
        refresh();
    }

    @Override
    public void basketCleared() {
        seatMap.deselectAll();
        refresh();
    }

    @Override
    public void voucherApplied() {
        refresh();
    }

    @Override
    public void seatsPaid(List<String> seats) {
        seatMap.markSold(seats);
        basket.clear();
    }

    public SeatMap seatMap() {
        return seatMap;
    }

    public Basket basket() {
        return basket;
    }

    public PriceSummary priceSummary() {
        return summary;
    }

    public PayButton payButton() {
        return payButton;
    }

    /** Jedno miejsce, które zna kolejność odświeżania: najpierw suma, potem przycisk. */
    private void refresh() {
        summary.recalculate(basket.seats());
        payButton.refresh(basket.seats().size(), summary.total());
    }

    public String render() {
        return seatMap.describe() + " " + basket.describe() + " " + summary.describe() + " " + payButton.describe();
    }
}
