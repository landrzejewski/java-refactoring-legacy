package pl.training.workshop.exercises.e10_showtime.step2;

import java.util.List;
import java.util.function.Consumer;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/**
 * Krok 2: korzeń kompozycji składa podsystemy i fasadę; klienci dostają tylko fasadę.
 * Zdarzenia sprzętu ({@code tick}, {@code obstacle}, {@code cleared}, {@code volume}, {@code emergency})
 * trafiają do sterownika sali bezpośrednio - to symulacja czujników, nie operacja klienta.
 */
public final class ShowtimeRunner {
    private final DeviceLog log = new DeviceLog();
    private final TicketScanner scanner = new TicketScanner(log);
    private final HallController hall = new HallController(log);
    private final Showtime showtime = new Showtime(scanner, hall, new AttendanceReport(log));
    private final BoxOfficePanel desk = new BoxOfficePanel(showtime);
    private final AutoScheduler auto = new AutoScheduler(showtime);

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
