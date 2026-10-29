package pl.training.workshop.exercises.e04_seatpicker.start;

import java.util.List;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;

/**
 * Start: klient sceny - sesja kasjera wykonuje skrypt operacji na koszyku miejsc i zwraca jego stan.
 * Błąd przerywa skrypt; wynik pokazuje komunikat i stan koszyka po błędzie.
 */
public final class SeatPickerSession {
    private final HallPlan plan;

    public SeatPickerSession(HallPlan plan) {
        this.plan = plan;
    }

    public String run(List<String> script) {
        SeatPicker picker = new SeatPicker(plan);
        try {
            for (String line : script) {
                picker.apply(line);
            }
            return picker.state();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return "ERROR: " + exception.getMessage() + " @ " + picker.state();
        }
    }
}
