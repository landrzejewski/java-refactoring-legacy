package pl.training.workshop.exercises.e10_showtime.start;

import java.util.List;
import java.util.function.Consumer;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/**
 * Start: korzeń kompozycji i symulator dnia w sali. Skrypt to polecenia klientów
 * ({@code desk start S1 Diuna}, {@code auto end S1}, {@code scan S1}) i zdarzenia sprzętu
 * ({@code tick}, {@code obstacle}, {@code cleared}, {@code volume 95}, {@code emergency}).
 * Wynikiem jest dziennik urządzeń; błąd polecenia to wpis {@code ERROR: ...}.
 */
public final class ShowtimeRunner {
    private final DeviceLog log = new DeviceLog();
    private final TicketScanner scanner = new TicketScanner(log);
    private final HallController hall = new HallController(log);
    private final AttendanceReport report = new AttendanceReport(log);
    private final BoxOfficePanel desk = new BoxOfficePanel(scanner, hall, report);
    private final AutoScheduler auto = new AutoScheduler(scanner, hall, report);

    public String run(List<String> script) {
        for (String line : script) {
            try {
                execute(line.trim().split("\\s+"));
            } catch (IllegalStateException | IllegalArgumentException exception) {
                log.add("ERROR: " + exception.getMessage());
            }
        }
        return log.dump();
    }

    private void execute(String[] words) {
        switch (words[0]) {
            case "scan" -> scanner.scan(words[1]);
            case "desk" -> client(words, desk::startShow, desk::endShow);
            case "auto" -> client(words, auto::startShow, auto::endShow);
            case "tick" -> hall.adTick();
            case "obstacle" -> hall.doorObstacle();
            case "cleared" -> hall.doorCleared();
            case "volume" -> hall.volume(Integer.parseInt(words[1]));
            case "emergency" -> hall.emergency();
            default -> throw new IllegalArgumentException("unknown command: " + String.join(" ", words));
        }
    }

    private static void client(String[] words, Consumer<ShowRequest> start, Consumer<String> end) {
        switch (words[1]) {
            case "start" -> start.accept(new ShowRequest(words[2], words[3]));
            case "end" -> end.accept(words[2]);
            default -> throw new IllegalArgumentException("unknown command: " + String.join(" ", words));
        }
    }
}
