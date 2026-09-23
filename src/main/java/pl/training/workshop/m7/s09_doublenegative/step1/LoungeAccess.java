package pl.training.workshop.m7.s09_doublenegative.step1;

import java.time.LocalDate;

/** Krok 1: LoungeAccess bez zmian - dodaliśmy tylko pozytywne predykaty w Customer i Voucher. */
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
