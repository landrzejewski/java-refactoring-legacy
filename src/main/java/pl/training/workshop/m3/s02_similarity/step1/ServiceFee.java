package pl.training.workshop.m3.s02_similarity.step1;

/**
 * Krok 1: po Inline Method z ServiceFee zostało tylko wyliczenie rodzajów.
 * Usuniemy je w kroku 2, gdy wywołujący przestaną z niego korzystać.
 */
public final class ServiceFee {
    public enum Kind { ONLINE_BOOKING, REFUND }

    private ServiceFee() {
    }
}
