package pl.training.workshop.m8.s07_adr.step1.pricing;

/** Krok 1: wynik cennika niesie informację o rabacie - zamiast efektu ubocznego mamy daną. */
public record Quote(double total, boolean groupDiscount) {
}
