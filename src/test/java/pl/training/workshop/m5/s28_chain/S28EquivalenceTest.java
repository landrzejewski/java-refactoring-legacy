package pl.training.workshop.m5.s28_chain;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Tabela decyzji zwrotu: każdy poziom, granice limitów i 24 h, kolejność reguł (AUTO bez limitu,
 * odrzucenie po seansie przed wszystkim) oraz walidacja kwoty przed łańcuchem.
 */
final class S28EquivalenceTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 20, 18, 0);

    @TestFactory
    Stream<DynamicTest> everyStepDecidesTheSame() {
        return Scene.<RefundRequest, String>variants()
                .variant("start", safe(new pl.training.workshop.m5.s28_chain.start.RefundApprover()::decide))
                .variant("step1", safe(new pl.training.workshop.m5.s28_chain.step1.RefundApprover()::decide))
                .variant("step2", safe(new pl.training.workshop.m5.s28_chain.step2.RefundApprover()::decide))
                .variant("step3", safe(new pl.training.workshop.m5.s28_chain.step3.RefundApprover()::decide))
                .expect("48 h przed: automatycznie", request("50.00", START.minusHours(48)), "APPROVED AUTO")
                .expect("dokładnie 24 h przed: automatycznie", request("50.00", START.minusHours(24)), "APPROVED AUTO")
                .expect("23 h 59 min przed: kasjer", request("50.00", START.minusHours(24).plusMinutes(1)),
                        "APPROVED CASHIER")
                .expect("limit kasjera włącznie", request("100.00", START.minusHours(2)), "APPROVED CASHIER")
                .expect("grosz ponad limit kasjera: kierownik", request("100.01", START.minusHours(2)), "APPROVED MANAGER")
                .expect("limit kierownika włącznie", request("1000.00", START.minusHours(2)), "APPROVED MANAGER")
                .expect("ponad limit kierownika: koniec łańcucha", request("1000.01", START.minusHours(2)),
                        "REJECTED brak uprawnionego zatwierdzajacego")
                .expect("AUTO nie ma limitu kwoty (kolejność reguł)", request("5000.00", START.minusDays(3)),
                        "APPROVED AUTO")
                .expect("w chwili rozpoczęcia seansu", request("10.00", START), "REJECTED seans juz sie rozpoczal")
                .expect("po seansie, nawet mała kwota", request("10.00", START.plusHours(1)),
                        "REJECTED seans juz sie rozpoczal")
                .expect("kwota zero", request("0.00", START.minusHours(2)), "ERROR amount must be positive")
                .expect("walidacja przed łańcuchem", request("-5.00", START.plusHours(1)),
                        "ERROR amount must be positive")
                .tests();
    }

    private static RefundRequest request(String amount, LocalDateTime requestedAt) {
        return new RefundRequest(Money.of(amount), START, requestedAt);
    }

    private static Function<RefundRequest, String> safe(Function<RefundRequest, RefundDecision> decide) {
        return request -> {
            try {
                return decide.apply(request).toString();
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }
}
