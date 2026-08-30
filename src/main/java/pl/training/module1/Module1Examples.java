package pl.training.module1;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class Module1Examples {
    private Module1Examples() {
    }

    public static void main(String[] args) {
        runLegacyOrderService();
        runRiskClassifier();
        runDiscountPolicy();
        runSalesCalculations();
    }

    private static void runLegacyOrderService() {
        OrderRepository repository = (orderId, total) ->
                System.out.printf("Saved order %s with total %s%n", orderId, total);
        MailGateway mailGateway = (recipient, body) ->
                System.out.printf("Sent to %s: %s%n", recipient, body);

        LegacyOrderService service = new LegacyOrderService(repository, mailGateway);
        Order order = new Order(
                UUID.fromString("9aa026a4-fc39-4af8-a008-d9b831b0ba59"),
                "customer@example.com",
                List.of(new OrderLine("BOOK-1", 2, new BigDecimal("100.00"))));

        Receipt receipt = service.placeOrder(order, "VIP", false, "PL");
        System.out.println("Receipt: " + receipt);
    }

    private static void runRiskClassifier() {
        OrderSummary order = new OrderSummary(
                new BigDecimal("1500.00"),
                true,
                List.of(new Item(true), new Item(false)));

        System.out.println("Risk level: " + RiskClassifier.riskLevel(order));
    }

    private static void runDiscountPolicy() {
        int discount = DiscountPolicy.discountPercent(100, true);
        System.out.println("Discount: " + discount + "%");
    }

    private static void runSalesCalculations() {
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal invoice = SalesCalculations.invoiceLineTotal(price, 1, true);
        BigDecimal quote = SalesCalculations.quoteLineTotal(price, 1, true);

        System.out.println("Invoice line total: " + invoice);
        System.out.println("Quote line total: " + quote);
    }
}
