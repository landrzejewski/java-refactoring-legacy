package pl.training.workshop.exercises.e10_showtime.step2;

/** Krok 2: raport frekwencji dla dystrybutora - rejestruje widzów na starcie seansu i zamknięcie seansu. */
final class AttendanceReport {
    private final DeviceLog log;

    public AttendanceReport(DeviceLog log) {
        this.log = log;
    }

    public void record(String screeningId, int attendance) {
        log.add("report: " + screeningId + " attendance " + attendance);
    }

    public void close(String screeningId) {
        log.add("report: " + screeningId + " closed");
    }
}
