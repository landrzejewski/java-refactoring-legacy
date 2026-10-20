package pl.training.workshop.m5.s32_mediator.step1;

/**
 * Stabilny punkt wejścia sceny: ekran wyboru miejsc. Krok 1: Extract Interface
 * SelectionMediator + Move Method - ekran (jak "dialog" z GoF) przejmuje reakcję na kliknięcie
 * miejsca. Pozostałe komponenty nadal są połączone wprost. Mapa dostaje mediatora
 * w konstruktorze ("this" przed końcem konstrukcji - bezpieczne, bo mediator działa dopiero
 * po akcji użytkownika).
 */
public final class SeatSelectionScreen implements SelectionMediator {
    private final SeatMap seatMap = new SeatMap(this);
    private final Basket basket = new Basket();
    private final PriceSummary summary = new PriceSummary();
    private final PayButton payButton = new PayButton();

    public SeatSelectionScreen() {
        basket.connect(seatMap, summary, payButton);
        summary.connect(basket, payButton);
        payButton.connect(seatMap, basket, summary);
    }

    @Override
    public void seatToggled(String seat, boolean selected) {
        if (selected) {
            basket.add(seat);
        } else {
            basket.drop(seat);
        }
        summary.recalculate(basket.seats());
        payButton.refresh(basket.seats().size(), summary.total());
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
