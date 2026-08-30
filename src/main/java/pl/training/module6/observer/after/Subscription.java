package pl.training.module6.observer.after;

@FunctionalInterface
public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
