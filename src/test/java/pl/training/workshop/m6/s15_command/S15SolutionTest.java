package pl.training.workshop.m6.s15_command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s15_command.step3.CashierConsole;
import pl.training.workshop.m6.s15_command.step3.ReportCommand;

/** Rejestr komend jest otwarty na nowe komendy bez zmiany dyspozytora. */
final class S15SolutionTest {
    @Test
    void newCommandIsJustARegistryEntry() {
        CashierConsole console = new CashierConsole(Map.of(
                "REPORT", new ReportCommand(),
                "HELLO", (args, till) -> "Dzien dobry " + args));
        assertEquals("Dzien dobry Anna", console.handle("hello Anna"));
        assertEquals("Nieznana komenda: SELL", console.handle("SELL 1 Diuna"));
    }
}
