package pl.training.workshop.m5.s32_mediator.start;

/**
 * Stabilny punkt wejścia sceny: ekran wyboru miejsc. Start: tylko tworzy komponenty
 * i łączy je "każdy z każdym" - 11 referencji między 4 komponentami.
 */
public final class SeatSelectionScreen {
    private final SeatMap seatMap = new SeatMap();
    private final Basket basket = new Basket();
    private final PriceSummary summary = new PriceSummary();
    private final PayButton payButton = new PayButton();

    public SeatSelectionScreen() {
        seatMap.connect(basket, summary, payButton);
        basket.connect(seatMap, summary, payButton);
        summary.connect(basket, payButton);
        payButton.connect(seatMap, basket, summary);
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

    public String render() {
        return seatMap.describe() + " " + basket.describe() + " " + summary.describe() + " " + payButton.describe();
    }
}
