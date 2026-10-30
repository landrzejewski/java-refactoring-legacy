package pl.training.workshop.exercises.e10_showtime.start

/** Start: raport frekwencji dla dystrybutora - rejestruje widzów na starcie seansu i zamknięcie seansu. */
class AttendanceReport(private val log: DeviceLog) {
    fun record(screeningId: String, attendance: Int) {
        log.add("report: $screeningId attendance $attendance")
    }

    fun close(screeningId: String) {
        log.add("report: $screeningId closed")
    }
}
