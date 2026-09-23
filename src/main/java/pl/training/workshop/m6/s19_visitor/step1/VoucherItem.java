package pl.training.workshop.m6.s19_visitor.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: voucher z accept. */
public record VoucherItem(String code, Money value) implements OrderItem {
    @Override
    public <R> R accept(OrderItemVisitor<R> visitor) {
        return visitor.visitVoucher(this);
    }
}
