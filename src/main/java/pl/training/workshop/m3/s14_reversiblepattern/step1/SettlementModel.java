package pl.training.workshop.m3.s14_reversiblepattern.step1;

import java.math.BigDecimal;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;

/**
 * Krok 1: Strategy - wspólny kontrakt modeli rozliczeń.
 * Wynik: kwota dla dystrybutora, skala 2, nieujemna, bez efektów ubocznych.
 */
public interface SettlementModel {
    BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue);
}
