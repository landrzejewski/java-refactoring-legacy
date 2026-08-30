package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.module2.OrderPlacementService.EventPublisher;
import pl.training.module2.OrderPlacementService.OrderDraft;
import pl.training.module2.OrderPlacementService.OrderPlaced;
import pl.training.module2.OrderPlacementService.OrderRepository;
import pl.training.module2.OrderPlacementService.PaymentGateway;
import pl.training.module2.OrderPlacementService.PlacedOrder;
import pl.training.module2.OrderPlacementService.ProductCatalog;

final class OrderPlacementServiceTest {
    @Test
    void placesOrderUsingStubFakeAndSpy() {
        ProductCatalog catalogStub = sku -> new BigDecimal("12.50");
        PaymentGateway paymentStub = (token, amount) -> "AUTH-7";
        InMemoryOrderRepository repositoryFake = new InMemoryOrderRepository();
        RecordingEventPublisher publisherSpy = new RecordingEventPublisher();
        OrderPlacementService service = new OrderPlacementService(
                catalogStub,
                paymentStub,
                repositoryFake,
                publisherSpy);

        PlacedOrder result = service.place("BOOK", 2, "TOKEN-1");

        assertEquals(new BigDecimal("25.00"), result.total());
        assertEquals("AUTH-7", result.authorizationId());
        assertEquals(
                new OrderDraft("BOOK", 2, new BigDecimal("25.00"), "AUTH-7"),
                repositoryFake.find(result.orderId()));
        assertEquals(
                List.of(new OrderPlaced(result.orderId(), new BigDecimal("25.00"))),
                publisherSpy.publishedEvents());
    }

    @Test
    void verifiesPaymentProtocolUsingMock() {
        ProductCatalog catalogStub = sku -> new BigDecimal("40.00");
        ExpectingPaymentGateway paymentMock = new ExpectingPaymentGateway(
                "TOKEN-2",
                new BigDecimal("120.00"),
                "AUTH-9");
        InMemoryOrderRepository repositoryFake = new InMemoryOrderRepository();
        EventPublisher publisherStub = event -> {
        };
        OrderPlacementService service = new OrderPlacementService(
                catalogStub,
                paymentMock,
                repositoryFake,
                publisherStub);

        service.place("COURSE", 3, "TOKEN-2");

        paymentMock.verify();
    }

    private static final class InMemoryOrderRepository implements OrderRepository {
        private final Map<Long, OrderDraft> orders = new LinkedHashMap<>();
        private long nextId = 1;

        @Override
        public long save(OrderDraft order) {
            long id = nextId++;
            orders.put(id, order);
            return id;
        }

        OrderDraft find(long orderId) {
            return orders.get(orderId);
        }
    }

    private static final class RecordingEventPublisher implements EventPublisher {
        private final List<OrderPlaced> events = new ArrayList<>();

        @Override
        public void publish(OrderPlaced event) {
            events.add(event);
        }

        List<OrderPlaced> publishedEvents() {
            return List.copyOf(events);
        }
    }

    private static final class ExpectingPaymentGateway implements PaymentGateway {
        private final String expectedToken;
        private final BigDecimal expectedAmount;
        private final String authorizationId;
        private int calls;

        private ExpectingPaymentGateway(
                String expectedToken,
                BigDecimal expectedAmount,
                String authorizationId) {
            this.expectedToken = expectedToken;
            this.expectedAmount = expectedAmount;
            this.authorizationId = authorizationId;
        }

        @Override
        public String charge(String paymentToken, BigDecimal amount) {
            assertEquals(expectedToken, paymentToken);
            assertEquals(expectedAmount, amount);
            calls++;
            return authorizationId;
        }

        void verify() {
            assertEquals(1, calls);
        }
    }
}
