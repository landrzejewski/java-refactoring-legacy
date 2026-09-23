package pl.training.workshop.m6.s19_visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s19_visitor.step2.OrderItem;
import pl.training.workshop.m6.s19_visitor.step2.OrderItemVisitor;
import pl.training.workshop.m6.s19_visitor.step2.SnackItem;
import pl.training.workshop.m6.s19_visitor.step2.TicketItem;
import pl.training.workshop.m6.s19_visitor.step2.VoucherItem;
import pl.training.workshop.shared.Money;

/** Macierz zmian: nowa operacja jest tania zarówno jako Visitor, jak i jako switch po sealed. */
final class S19SolutionTest {
    @Test
    void newOperationIsANewVisitor() {
        OrderItemVisitor<String> vatGroup = new OrderItemVisitor<>() {
            @Override
            public String visitTicket(TicketItem ticket) {
                return "B";
            }

            @Override
            public String visitSnack(SnackItem snack) {
                return "A";
            }

            @Override
            public String visitVoucher(VoucherItem voucher) {
                return "-";
            }
        };
        List<OrderItem> items = List.of(new TicketItem("Diuna", "IMAX", Money.of("40.00")),
                new SnackItem("Cola", Money.of("9.00")), new VoucherItem("KINO20", Money.of("20.00")));
        assertEquals(List.of("B", "A", "-"), items.stream().map(item -> item.accept(vatGroup)).toList());
    }

    @Test
    void newOperationIsANewSwitchInJava25() {
        var items = List.<pl.training.workshop.m6.s19_visitor.step3.OrderItem>of(
                new pl.training.workshop.m6.s19_visitor.step3.TicketItem("Diuna", "IMAX", Money.of("40.00")),
                new pl.training.workshop.m6.s19_visitor.step3.VoucherItem("KINO20", Money.of("20.00")));
        assertEquals(List.of("B", "-"), items.stream().map(item -> switch (item) {
            case pl.training.workshop.m6.s19_visitor.step3.TicketItem ticket -> "B";
            case pl.training.workshop.m6.s19_visitor.step3.SnackItem snack -> "A";
            case pl.training.workshop.m6.s19_visitor.step3.VoucherItem voucher -> "-";
        }).toList());
    }
}
