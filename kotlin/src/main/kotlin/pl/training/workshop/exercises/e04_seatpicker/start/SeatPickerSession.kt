package pl.training.workshop.exercises.e04_seatpicker.start

import pl.training.workshop.exercises.e04_seatpicker.HallPlan

/**
 * Start: klient sceny - sesja kasjera wykonuje skrypt operacji na koszyku miejsc i zwraca jego stan.
 * Błąd przerywa skrypt; wynik pokazuje komunikat i stan koszyka po błędzie.
 */
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
