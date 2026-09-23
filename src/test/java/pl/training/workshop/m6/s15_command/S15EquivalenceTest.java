package pl.training.workshop.m6.s15_command;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Sesja kasjera: ta sama sekwencja poleceń daje te same odpowiedzi i ten sam stan kasy. */
final class S15EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepHandlesTheSessionTheSame() {
        return Scene.<List<String>, String>variants()
                .variant("start", session(() -> new pl.training.workshop.m6.s15_command.start.CashierConsole()::handle))
                .variant("step1", session(() -> new pl.training.workshop.m6.s15_command.step1.CashierConsole()::handle))
                .variant("step2", session(() -> new pl.training.workshop.m6.s15_command.step2.CashierConsole()::handle))
                .variant("step3", session(() -> new pl.training.workshop.m6.s15_command.step3.CashierConsole()::handle))
                .expect("sprzedaż, zwrot, raport", List.of(
                        "SELL 2 Diuna", "sell 1 Kraina Lodu", "REFUND Diuna", "REPORT"), """
                        Sprzedano 2 x Diuna = 80.00
                        Sprzedano 1 x Kraina Lodu = 32.00
                        Zwrot 1 x Diuna = 40.00
                        Kasa: 72.00, biletow: 2""")
                .expect("błędy nie zmieniają stanu", List.of(
                        "SELL Diuna", "SELL 2 Batman", "REFUND Amator", "PRINT", "  report  "), """
                        Blad: SELL <liczba> <tytul>
                        Blad: nieznany film Batman
                        Blad: brak biletow do zwrotu
                        Nieznana komenda: PRINT
                        Kasa: 0.00, biletow: 0""")
                .tests();
    }

    private static Function<List<String>, String> session(Supplier<Function<String, String>> console) {
        return lines -> {
            Function<String, String> handle = console.get();
            return String.join("\n", lines.stream().map(handle).toList());
        };
    }
}
