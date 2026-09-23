package pl.training.workshop.m7.s09_doublenegative.step1;

/** Krok 1: pozytywny predykat vip() delegujący do starego komponentu - dokładne dopełnienie. */
public record Customer(String email, boolean notVip) {
    public boolean vip() {
        return !notVip;
    }
}
