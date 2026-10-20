package pl.training.module3.incremental;

/**
 * Czysta granica obliczeniowa używana podczas migracji. Implementacje nie
 * wykonują operacji wejścia-wyjścia ani nie modyfikują zewnętrznego stanu.
 */
@FunctionalInterface
public interface PricingEngine {
    PriceQuote quote(PriceRequest request);
}
