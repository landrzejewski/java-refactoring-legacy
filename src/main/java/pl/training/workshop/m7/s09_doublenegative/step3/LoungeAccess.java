package pl.training.workshop.m7.s09_doublenegative.step3;

import java.time.LocalDate;

/** Krok 3 (rozwiązanie): LoungeAccess jak w kroku 2 - czyta się bez odwracania w głowie. */
public final class LoungeAccess {
    public boolean canEnter(Customer customer, Voucher voucher, LocalDate today) {
        if (customer.vip()) {
            return true;
        }
        if (voucher == null) {
            return false;
        }
        if (voucher.isExpired(today)) {
            return false;
        }
        return true;
    }

    public String badge(Customer customer) {
        return customer.vip() ? "VIP" : "STANDARD";
    }
}
