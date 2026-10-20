package pl.training.workshop.m5.s32_mediator.step3;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: przycisk zna tylko mediatora - pilnuje własnej reguły (aktywny dla 1-4 miejsc)
 * i zgłasza chęć płatności. Dane o koszyku i kwocie zbiera mediator.
 */
public final class PayButton {
    private static final int MAX_SEATS = 4;

    private boolean enabled;
    private String label = "Wybierz miejsca";
    private final SelectionMediator mediator;

    public PayButton(SelectionMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    /** Akcja użytkownika: płatność za zawartość koszyka. */
    public String press() {
        if (!enabled) {
            return "Nieaktywny: " + label;
        }
        return mediator.payRequested();
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
