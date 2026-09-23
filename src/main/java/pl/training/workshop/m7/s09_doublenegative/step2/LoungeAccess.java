package pl.training.workshop.m7.s09_doublenegative.step2;

import java.time.LocalDate;

/** Krok 2: migracja użyć po jednym - każde !negatyw() zamienione na pozytywny predykat. */
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
