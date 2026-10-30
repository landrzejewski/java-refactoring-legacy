package pl.training.workshop.exercises.e04_seatpicker

/** Stabilny kontrakt sceny: układ sali i skrypt operacji kasjera, np. "SELECT 6-4", "UNDO". */
data class PickerRun(val plan: HallPlan, val script: List<String>) {
    companion object {
        fun standard(vararg script: String): PickerRun = PickerRun(HallPlan.standard(), script.toList())

        fun almostFull(vararg script: String): PickerRun = PickerRun(HallPlan.almostFull(), script.toList())
    }
}
