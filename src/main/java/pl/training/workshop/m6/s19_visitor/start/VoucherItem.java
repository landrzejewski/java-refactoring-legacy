package pl.training.workshop.m6.s19_visitor.start;

import pl.training.workshop.shared.Money;

/** Start: voucher - pomniejsza kwotę do zapłaty, bez VAT. */
public record VoucherItem(String code, Money value) implements OrderItem {
}
