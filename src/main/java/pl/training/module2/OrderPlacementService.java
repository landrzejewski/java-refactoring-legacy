package pl.training.module2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class OrderPlacementService {
    private final ProductCatalog catalog;
    private final PaymentGateway paymentGateway;
    private final OrderRepository repository;
    private final EventPublisher eventPublisher;

    public OrderPlacementService(
            ProductCatalog catalog,
            PaymentGateway paymentGateway,
            OrderRepository repository,
            EventPublisher eventPublisher) {
        this.catalog = Objects.requireNonNull(catalog);
        this.paymentGateway = Objects.requireNonNull(paymentGateway);
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public PlacedOrder place(
            String sku,
            int quantity,
            String paymentToken) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        BigDecimal total = catalog.priceFor(sku)
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
        String authorizationId = paymentGateway.charge(paymentToken, total);
        long orderId = repository.save(
                new OrderDraft(sku, quantity, total, authorizationId));

        eventPublisher.publish(new OrderPlaced(orderId, total));
        return new PlacedOrder(orderId, total, authorizationId);
    }

    public interface ProductCatalog {
        BigDecimal priceFor(String sku);
    }

    public interface PaymentGateway {
        String charge(String paymentToken, BigDecimal amount);
    }

    public interface OrderRepository {
        long save(OrderDraft order);
    }

    public interface EventPublisher {
        void publish(OrderPlaced event);
    }

    public record OrderDraft(
            String sku,
            int quantity,
            BigDecimal total,
            String authorizationId) {
    }

    public record OrderPlaced(long orderId, BigDecimal total) {
    }

    public record PlacedOrder(
            long orderId,
            BigDecimal total,
            String authorizationId) {
    }
}
