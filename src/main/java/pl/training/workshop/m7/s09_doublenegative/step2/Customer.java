package pl.training.workshop.m7.s09_doublenegative.step2;

/** Krok 1 (bez zmian w kroku 2): pozytywny predykat vip() delegujący do starego komponentu. */
public record Customer(String email, boolean notVip) {
    public boolean vip() {
        return !notVip;
    }
}
