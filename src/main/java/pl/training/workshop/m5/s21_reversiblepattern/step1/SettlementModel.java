package pl.training.workshop.m5.s21_reversiblepattern.step1;

import java.math.BigDecimal;

import pl.training.workshop.m5.s21_reversiblepattern.Deal;

/**
 * Krok 1: Strategy - wspólny kontrakt modeli rozliczeń.
 * Wynik: kwota dla dystrybutora, skala 2, nieujemna, bez efektów ubocznych.
 */
public interface SettlementModel {
    BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue);
}
