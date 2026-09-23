package pl.training.workshop.m6.s19_visitor.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: operacje jako wyczerpujące switche po sealed hierarchii - bez default. Każda operacja
 * w jednym miejscu (jak Visitor), bez ceremonii accept/visit i bez double dispatch.
 */
public final class ReceiptPrinter {
    public String print(List<OrderItem> items) {
        StringBuilder text = new StringBuilder();
        Money total = Money.ZERO;
        Money vat = Money.ZERO;
        for (OrderItem item : items) {
            text.append(line(item)).append('\n');
            total = total.plus(amount(item));
            vat = vat.plus(vat(item));
        }
        return text.append("Razem: ").append(total.max(Money.ZERO)).append('\n')
                .append("VAT: ").append(vat).append('\n').toString();
    }

    static String line(OrderItem item) {
        return switch (item) {
            case TicketItem(String title, String format, Money price) ->
                    "Bilet " + title + " " + format + " " + price;
            case SnackItem(String name, Money price) -> name + " " + price;
            case VoucherItem(String code, Money value) -> "Voucher " + code + " -" + value;
        };
    }

    static Money amount(OrderItem item) {
        return switch (item) {
            case TicketItem ticket -> ticket.price();
            case SnackItem snack -> snack.price();
            case VoucherItem voucher -> Money.ZERO.minus(voucher.value());
        };
    }

    static Money vat(OrderItem item) {
        return switch (item) {
            case TicketItem ticket -> vatOf(ticket.price(), 8);
            case SnackItem snack -> vatOf(snack.price(), 23);
            case VoucherItem voucher -> Money.ZERO;
        };
    }

    private static Money vatOf(Money gross, int rate) {
        return new Money(gross.amount().multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100L + rate), 2, RoundingMode.HALF_UP));
    }
}
