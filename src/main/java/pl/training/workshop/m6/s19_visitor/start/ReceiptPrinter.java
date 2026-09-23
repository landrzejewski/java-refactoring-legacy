package pl.training.workshop.m6.s19_visitor.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Start: trzy operacje (linia paragonu, kwota, VAT), każda z łańcuchem instanceof zakończonym
 * wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu - i wybucha na kasie.
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

    private String line(OrderItem item) {
        if (item instanceof TicketItem ticket) {
            return "Bilet " + ticket.title() + " " + ticket.format() + " " + ticket.price();
        } else if (item instanceof SnackItem snack) {
            return snack.name() + " " + snack.price();
        } else if (item instanceof VoucherItem voucher) {
            return "Voucher " + voucher.code() + " -" + voucher.value();
        }
        throw new IllegalArgumentException("unknown item: " + item);
    }

    private Money amount(OrderItem item) {
        if (item instanceof TicketItem ticket) {
            return ticket.price();
        } else if (item instanceof SnackItem snack) {
            return snack.price();
        } else if (item instanceof VoucherItem voucher) {
            return Money.ZERO.minus(voucher.value());
        }
        throw new IllegalArgumentException("unknown item: " + item);
    }

    private Money vat(OrderItem item) {
        if (item instanceof TicketItem ticket) {
            return vatOf(ticket.price(), 8);
        } else if (item instanceof SnackItem snack) {
            return vatOf(snack.price(), 23);
        } else if (item instanceof VoucherItem) {
            return Money.ZERO;
        }
        throw new IllegalArgumentException("unknown item: " + item);
    }

    /** VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka). */
    private static Money vatOf(Money gross, int rate) {
        return new Money(gross.amount().multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100L + rate), 2, RoundingMode.HALF_UP));
    }
}
