package pl.training.workshop.m7.s09_doublenegative.start;

import java.time.LocalDate;

/** Start: podwójne zaprzeczenia - !customer.notVip() i !voucher.isNotExpired(today). */
public final class LoungeAccess {
    public boolean canEnter(Customer customer, Voucher voucher, LocalDate today) {
        if (!customer.notVip()) {
            return true;
        }
        if (voucher == null) {
            return false;
        }
        if (!voucher.isNotExpired(today)) {
            return false;
        }
        return true;
    }

    public String badge(Customer customer) {
        return !customer.notVip() ? "VIP" : "STANDARD";
    }
}
