package pl.training.workshop.m6.s15_command.step2;

import java.util.Locale;

/**
 * Krok 2: Extract Class dla każdej gałęzi - komendy jako obiekty, stan w Till.
 * Dyspozytor warunkowy jeszcze zostaje; zmieniamy jedną rzecz naraz.
 */
public final class CashierConsole {
    private final Till till = new Till();
    private final ConsoleCommand sell = new SellCommand();
    private final ConsoleCommand refund = new RefundCommand();
    private final ConsoleCommand report = new ReportCommand();

    public String handle(String line) {
        String[] parts = line.strip().split(" ", 2);
        String command = parts[0].toUpperCase(Locale.ROOT);
        String args = parts.length > 1 ? parts[1] : "";
        if (command.equals("SELL")) {
            return sell.execute(args, till);
        } else if (command.equals("REFUND")) {
            return refund.execute(args, till);
        } else if (command.equals("REPORT")) {
            return report.execute(args, till);
        }
        return "Nieznana komenda: " + parts[0];
    }
}
