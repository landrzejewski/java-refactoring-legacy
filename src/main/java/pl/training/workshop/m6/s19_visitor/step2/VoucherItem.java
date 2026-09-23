package pl.training.workshop.m6.s19_visitor.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: voucher z accept. */
public record VoucherItem(String code, Money value) implements OrderItem {
    @Override
    public <R> R accept(OrderItemVisitor<R> visitor) {
        return visitor.visitVoucher(this);
    }
}
