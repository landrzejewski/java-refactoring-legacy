package pl.training.workshop.m7.s09_doublenegative.step3;

/**
 * Krok 3: odwrócenie delegacji - komponent nazywa się vip, negatyw zniknął.
 * Uwaga: zmienia się znaczenie argumentu konstruktora kanonicznego (true = VIP),
 * a gdyby rekord trafiał do JSON-a lub bazy, zmieniłaby się też granica - to wymaga migracji.
 */
public record Customer(String email, boolean vip) {
}
