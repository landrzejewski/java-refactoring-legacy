package pl.training.workshop.exercises.e10_showtime.step2

/** Krok 2: raport frekwencji dla dystrybutora - rejestruje widzów na starcie seansu i zamknięcie seansu. */
internal class AttendanceReport(private val log: DeviceLog) {
    fun record(screeningId: String, attendance: Int) {
        log.add("report: $screeningId attendance $attendance")
    }

    fun close(screeningId: String) {
        log.add("report: $screeningId closed")
    }
}
