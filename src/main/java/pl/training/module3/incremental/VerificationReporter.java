package pl.training.module3.incremental;

@FunctionalInterface
public interface VerificationReporter {
    void report(VerificationEvent event);

    static VerificationReporter ignoring() {
        return event -> {
        };
    }
}
