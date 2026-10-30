package pl.training.workshop.exercises.e04_seatpicker.step1

import pl.training.workshop.exercises.e04_seatpicker.HallPlan

/** Krok 1: klient sceny - bez zmian. */
class SeatPickerSession(private val plan: HallPlan) {
    fun run(script: List<String>): String {
        val picker = SeatPicker(plan)
        return try {
            for (line in script) {
                picker.apply(line)
            }
            picker.state()
        } catch (exception: IllegalArgumentException) {
            "ERROR: ${exception.message} @ ${picker.state()}"
        } catch (exception: IllegalStateException) {
            "ERROR: ${exception.message} @ ${picker.state()}"
        }
    }
}
