package pl.training.workshop.m6.s15_command.step3;

import java.util.Locale;
import java.util.Map;

/**
 * Krok 3: Replace Conditional Dispatcher with Command - rejestr komend zamiast if.
 * Równoważne, bo klucze są rozłączne; normalizacja klucza (toUpperCase ROOT) zachowana.
 */
public final class CashierConsole {
    private final Till till = new Till();
    private final Map<String, ConsoleCommand> commands;

    public CashierConsole() {
        this(Map.of(
                "SELL", new SellCommand(),
                "REFUND", new RefundCommand(),
                "REPORT", new ReportCommand()));
    }

    public CashierConsole(Map<String, ConsoleCommand> commands) {
        this.commands = Map.copyOf(commands);
    }

    public String handle(String line) {
        String[] parts = line.strip().split(" ", 2);
        ConsoleCommand command = commands.get(parts[0].toUpperCase(Locale.ROOT));
        if (command == null) {
            return "Nieznana komenda: " + parts[0];
        }
        return command.execute(parts.length > 1 ? parts[1] : "", till);
    }
}
