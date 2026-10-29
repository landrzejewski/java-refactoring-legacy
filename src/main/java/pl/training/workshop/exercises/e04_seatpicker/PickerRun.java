package pl.training.workshop.exercises.e04_seatpicker;

import java.util.List;

/** Stabilny kontrakt sceny: układ sali i skrypt operacji kasjera, np. "SELECT 6-4", "UNDO". */
public record PickerRun(HallPlan plan, List<String> script) {
    public static PickerRun standard(String... script) {
        return new PickerRun(HallPlan.standard(), List.of(script));
    }

    public static PickerRun almostFull(String... script) {
        return new PickerRun(HallPlan.almostFull(), List.of(script));
    }
}
