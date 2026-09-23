package pl.training.workshop.m7.s08_designbycontract.step2;

/**
 * Jawne kontrole kontraktu - działają zawsze, w przeciwieństwie do assert (wymaga -ea).
 * require: obowiązek klienta (IllegalArgumentException),
 * ensure: gwarancja operacji (IllegalStateException).
 */
final class Contracts {
    private Contracts() {
    }

    static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    static void ensure(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
