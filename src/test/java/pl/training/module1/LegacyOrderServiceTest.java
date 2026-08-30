package pl.training.module1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

final class LegacyOrderServiceTest {
    @Test
    void placesVipOrderAndInvokesExternalCollaborators() {
        AtomicReference<BigDecimal> savedTotal = new AtomicReference<>();
        AtomicReference<String> sentMessage = new AtomicReference<>();
        OrderRepository repository = (orderId, total) -> savedTotal.set(total);
        MailGateway mailGateway = (recipient, body) -> sentMessage.set(body);
        LegacyOrderService service = new LegacyOrderService(repository, mailGateway);
        Order order = new Order(
                UUID.fromString("9aa026a4-fc39-4af8-a008-d9b831b0ba59"),
                "customer@example.com",
                List.of(new OrderLine("BOOK-1", 2, new BigDecimal("100.00"))));

        Receipt receipt = service.placeOrder(order, "VIP", false, "PL");

        assertEquals(new BigDecimal("236.39"), receipt.total());
        assertEquals(receipt.total(), savedTotal.get());
        assertEquals("Order total: 236.39", sentMessage.get());
    }
}
