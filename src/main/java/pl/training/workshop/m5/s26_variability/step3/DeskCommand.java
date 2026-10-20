package pl.training.workshop.m5.s26_variability.step3;

/**
 * Krok 3 (bez zmian od kroku 2): Command - żądanie kasjera jako obiekt. Wykonanie, cofnięcie i wpis do dziennika
 * są w jednym miejscu; polecenie pamięta tylko to, czego potrzebuje do cofnięcia.
 */
public interface DeskCommand {
    String execute(BookingLedger ledger);

    void undo(BookingLedger ledger);

    String journalEntry();

    /** Wspólna walidacja poleceń "OPERACJA &lt;id&gt;". */
    static String idFrom(String[] args, String operation) {
        if (args.length < 2) {
            throw new IllegalArgumentException(operation + " <id>");
        }
        return args[1];
    }
}
