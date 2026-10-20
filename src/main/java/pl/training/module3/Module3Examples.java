package pl.training.module3;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import pl.training.module3.OrderPlacementService.PlacedOrder;
import pl.training.module3.breakdependencies.after.DeploymentWindowService;
import pl.training.module3.breakdependencies.after.StandardMaintenanceWindows;
import pl.training.module3.incremental.CandidatePricingEngine;
import pl.training.module3.incremental.LegacyPricingEngineAdapter;
import pl.training.module3.incremental.MigratingPricingEngine;
import pl.training.module3.incremental.MigrationMode;
import pl.training.module3.incremental.PriceRequest;
import pl.training.module3.incremental.VerificationEvent;

public final class Module3Examples {
    private Module3Examples() {
    }

    public static void main(String[] args) {
        runBehaviorPreservingRefactoring();
        runTestDoubleExample();
        runExplicitSeamExample();
        runBreakDependenciesExample();
        runParallelVerificationExample();
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

    private static void runBreakDependenciesExample() {
        var windowService = new DeploymentWindowService(
                new StandardMaintenanceWindows());
        System.out.println("Break Dependencies: "
                + windowService.schedule("payments", 2));
    }

    private static void runParallelVerificationExample() {
        var events = new ArrayList<VerificationEvent>();
        var engine = new MigratingPricingEngine(
                new LegacyPricingEngineAdapter(),
                new CandidatePricingEngine(),
                events::add,
                MigrationMode.VERIFY);

        var quote = engine.quote(new PriceRequest(
                new BigDecimal("19.99"),
                3,
                new BigDecimal("0.10")));

        System.out.println("Parallel verification: " + quote.netAmount()
                + "/" + events.getFirst().getClass().getSimpleName());
    }
}
