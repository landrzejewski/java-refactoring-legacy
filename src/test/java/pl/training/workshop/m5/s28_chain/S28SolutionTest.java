package pl.training.workshop.m5.s28_chain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s28_chain.step3.AfterScreeningHandler;
import pl.training.workshop.m5.s28_chain.step3.AutoRefundHandler;
import pl.training.workshop.m5.s28_chain.step3.CashierHandler;
import pl.training.workshop.m5.s28_chain.step3.ManagerHandler;
import pl.training.workshop.m5.s28_chain.step3.NoApproverHandler;
import pl.training.workshop.m5.s28_chain.step3.RefundApprover;
import pl.training.workshop.m5.s28_chain.step3.RefundHandler;
import pl.training.workshop.shared.Money;

/** Łańcuch: jawnie składany, ogniwo zatrzymuje albo przekazuje - w odróżnieniu od dekoratora. */
final class S28SolutionTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 20, 18, 0);

    @Test
    void chainIsAssembledExplicitlySoAFestivalCanSkipAutoRefunds() {
        RefundApprover festival = new RefundApprover(
                new AfterScreeningHandler(new CashierHandler(Money.of("100.00"), new NoApproverHandler())));
        assertEquals("APPROVED CASHIER", festival.decide(request("50.00", START.minusDays(3))).toString());
        assertEquals("REJECTED brak uprawnionego zatwierdzajacego",
                festival.decide(request("500.00", START.minusDays(3))).toString());
    }

    @Test
    void handlerThatDecidesStopsTheChainUnlikeADecorator() {
        AtomicInteger calls = new AtomicInteger();
        RefundHandler spy = request -> {
            calls.incrementAndGet();
            return RefundDecision.rejected("spy");
        };
        CashierHandler cashier = new CashierHandler(Money.of("100.00"), spy);

        assertEquals("APPROVED CASHIER", cashier.handle(request("40.00", START.minusHours(2))).toString());
        assertEquals(0, calls.get(), "obsłużone żądanie nie idzie dalej");

        assertEquals("REJECTED spy", cashier.handle(request("400.00", START.minusHours(2))).toString());
        assertEquals(1, calls.get(), "nieobsłużone żądanie idzie dokładnie do następnika");
    }

    @Test
    void everyLinkKnowsOnlyItsSuccessor() {
        for (Class<?> link : List.of(AfterScreeningHandler.class, AutoRefundHandler.class,
                CashierHandler.class, ManagerHandler.class)) {
            long successors = Arrays.stream(link.getDeclaredFields())
                    .map(Field::getType)
                    .filter(RefundHandler.class::equals)
                    .count();
            assertEquals(1, successors, link.getSimpleName());
        }
        assertEquals(0, NoApproverHandler.class.getDeclaredFields().length, "koniec łańcucha nie ma następnika");
    }

    private static RefundRequest request(String amount, LocalDateTime requestedAt) {
        return new RefundRequest(Money.of(amount), START, requestedAt);
    }
}
