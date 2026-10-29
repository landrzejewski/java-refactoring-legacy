package pl.training.workshop.exercises.e04_seatpicker.step2;

import java.util.List;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;

/** Krok 2: klient sceny - bez zmian. */
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
