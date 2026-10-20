package pl.training.workshop.m5.s32_mediator.step3;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Stabilny punkt wejścia sceny. Krok 3: ekran jest jedynym miejscem, które zna wszystkie cztery
 * komponenty; każdy komponent zna tylko interfejs SelectionMediator (4 referencje zamiast 11).
 * Nowy komponent (np. licznik czasu rezerwacji) to zmiana w mediatorze, a nie w czterech klasach.
 */
public final class SeatSelectionScreen implements SelectionMediator {
    private final SeatMap seatMap = new SeatMap(this);
    private final Basket basket = new Basket(this);
    private final PriceSummary summary = new PriceSummary(this);
    private final PayButton payButton = new PayButton(this);

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
    public String payRequested() {
        List<String> paid = basket.seats();
        Money amount = summary.total();
        seatMap.markSold(paid);
        basket.clear();
        return "Zaplacono " + amount + " za " + String.join(",", paid);
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
