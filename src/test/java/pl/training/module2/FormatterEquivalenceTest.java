package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class FormatterEquivalenceTest {
    private final LegacyInvoiceFormatter legacy = new LegacyInvoiceFormatter();
    private final InvoiceFormatter refactored = new InvoiceFormatter();

    @ParameterizedTest
    @MethodSource("representativeInvoices")
    void refactoringPreservesObservedOutput(
            String customer,
            List<InvoiceLine> lines) {
        assertEquals(
                legacy.format(customer, lines),
                refactored.format(customer, lines));
    }

    private static Stream<Arguments> representativeInvoices() {
        return Stream.of(
                Arguments.of("Acme", List.of()),
                Arguments.of(null, List.of(
                        new InvoiceLine("BOOK", 1, new BigDecimal("10.00")))),
                Arguments.of("vip", List.of(
                        new InvoiceLine("A", 3, new BigDecimal("0.10")),
                        new InvoiceLine("B", 2, new BigDecimal("19.995")))));
    }
}
