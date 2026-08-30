package pl.training.module2;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import pl.training.module2.OrderPlacementService.PlacedOrder;

public final class Module2Examples {
    private Module2Examples() {
    }

    public static void main(String[] args) {
        runBehaviorPreservingRefactoring();
        runTestDoubleExample();
        runExplicitSeamExample();
    }

    private static void runBehaviorPreservingRefactoring() {
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("BOOK", 2, new BigDecimal("19.99")),
                new InvoiceLine("PEN", 1, new BigDecimal("5.00")));
        String before = new LegacyInvoiceFormatter().format("Acme", lines);
        String after = new InvoiceFormatter().format("Acme", lines);

        System.out.println("Formatter outputs equal: " + before.equals(after));
    }

    private static void runTestDoubleExample() {
        OrderPlacementService service = new OrderPlacementService(
                sku -> new BigDecimal("12.50"),
                (token, amount) -> "AUTH-DEMO",
                order -> 1L,
                event -> System.out.println("Published event: " + event));

        PlacedOrder order = service.place("BOOK", 2, "TOKEN-DEMO");
        System.out.println("Placed order: " + order);
    }

    private static void runExplicitSeamExample() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        ReminderService service = new ReminderService(
                clock,
                (email, renewalDate) -> System.out.printf(
                        "Reminder: %s renews on %s%n",
                        email,
                        renewalDate));

        service.sendRenewalReminder(new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 6)));
    }
}
