package pl.training.workshop.m6.s19_visitor.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: voucher - czyste dane, bez accept. */
public record VoucherItem(String code, Money value) implements OrderItem {
}
