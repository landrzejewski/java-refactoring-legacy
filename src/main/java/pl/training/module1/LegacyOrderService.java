package pl.training.module1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public final class LegacyOrderService {
    private final OrderRepository repository;
    private final MailGateway mailGateway;

    public LegacyOrderService(
            OrderRepository repository,
            MailGateway mailGateway) {
        this.repository = repository;
        this.mailGateway = mailGateway;
    }

    public Receipt placeOrder(
            Order order,
            String customerType,
            boolean express,
            String destinationCountry) {

        if (order == null || order.lines() == null || order.lines().isEmpty()) {
            throw new IllegalArgumentException("Order must contain lines");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderLine line : order.lines()) {
            BigDecimal lineValue = line.unitPrice()
                    .multiply(BigDecimal.valueOf(line.quantity()));

            if ("VIP".equals(customerType)) {
                lineValue = lineValue.multiply(new BigDecimal("0.90"));
            }

            if (line.quantity() >= 10) {
                lineValue = lineValue.multiply(new BigDecimal("0.95"));
            }

            subtotal = subtotal.add(lineValue);
        }

        BigDecimal shipping;
        if (express) {
            shipping = new BigDecimal("39.99");
        } else if (subtotal.compareTo(new BigDecimal("200.00")) >= 0) {
            shipping = BigDecimal.ZERO;
        } else {
            shipping = new BigDecimal("14.99");
        }

        BigDecimal tax;
        if ("PL".equals(destinationCountry)) {
            tax = subtotal.multiply(new BigDecimal("0.23"));
        } else if ("DE".equals(destinationCountry)) {
            tax = subtotal.multiply(new BigDecimal("0.19"));
        } else {
            tax = BigDecimal.ZERO;
        }

        BigDecimal total = subtotal
                .add(shipping)
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        repository.save(order.id(), total);
        mailGateway.send(order.customerEmail(), "Order total: " + total);

        return new Receipt(order.id(), total);
    }
}

record Order(
        UUID id,
        String customerEmail,
        List<OrderLine> lines) {
}

record OrderLine(
        String sku,
        int quantity,
        BigDecimal unitPrice) {
}

record Receipt(UUID orderId, BigDecimal total) {
}

interface OrderRepository {
    void save(UUID orderId, BigDecimal total);
}

interface MailGateway {
    void send(String recipient, String body);
}
