package pl.training.workshop.m5.s09_observer.step3;

/** Krok 3: uchwyt subskrypcji - close() wyrejestrowuje, idempotentnie. */
@FunctionalInterface
public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
