package pl.training.workshop.m5.s32_mediator.step2;

import java.util.List;
import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: przycisk zgłasza płatność mediatorowi (to on oznacza miejsca i czyści koszyk),
 * ale nadal sam PYTA koszyk i podsumowanie o dane - zostały mu dwie bezpośrednie referencje.
 */
public final class PayButton {
    private static final int MAX_SEATS = 4;

    private boolean enabled;
    private String label = "Wybierz miejsca";
    private final SelectionMediator mediator;
    private Basket basket;
    private PriceSummary summary;

    public PayButton(SelectionMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    void connect(Basket basket, PriceSummary summary) {
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
        mediator.seatsPaid(paid);
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
