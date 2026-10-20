package pl.training.workshop.m5.s26_variability.step3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Krok 3: trzy osie, trzy wzorce, zero mieszania. Pulpit zna tylko polecenia (Command)
 * i ich historię; cena biletu to Strategy wybierana po typie; cykl życia to State w Booking.
 * Nowy typ biletu, nowa operacja i nowy status zmieniają po jednym, rozłącznym miejscu.
 */
public final class BoxOfficeDesk {
    private static final Map<String, Function<String[], DeskCommand>> COMMANDS = Map.of(
            "BOOK", BookCommand::from,
            "PAY", PayCommand::from,
            "USE", UseCommand::from,
            "CANCEL", CancelCommand::from);

    private final BookingLedger ledger = new BookingLedger();
    private final List<DeskCommand> history = new ArrayList<>();

    public String handle(String line) {
        String[] args = line.strip().split(" +");
        String operation = args[0].toUpperCase(Locale.ROOT);
        try {
            return switch (operation) {
                case "UNDO" -> undo();
                case "REPORT" -> report();
                default -> execute(operation, args);
            };
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return "Blad: " + exception.getMessage();
        }
    }

    private String execute(String operation, String[] args) {
        Function<String[], DeskCommand> factory = COMMANDS.get(operation);
        if (factory == null) {
            return "Nieznana operacja: " + args[0];
        }
        DeskCommand command = factory.apply(args);
        String result = command.execute(ledger);
        history.add(command);
        return result;
    }

    private String undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("nothing to undo");
        }
        DeskCommand last = history.removeLast();
        last.undo(ledger);
        return "Cofnieto: " + last.journalEntry();
    }

    private String report() {
        String log = history.isEmpty() ? "-" : history.stream()
                .map(DeskCommand::journalEntry)
                .collect(Collectors.joining(", "));
        return ledger.summary() + "; dziennik: " + log;
    }
}
