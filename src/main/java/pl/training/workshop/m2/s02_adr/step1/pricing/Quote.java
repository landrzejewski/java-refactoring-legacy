package pl.training.workshop.m2.s02_adr.step1.pricing;

/** Krok 1: wynik cennika niesie informację o rabacie - zamiast efektu ubocznego mamy daną. */
public record Quote(double total, boolean groupDiscount) {
}
