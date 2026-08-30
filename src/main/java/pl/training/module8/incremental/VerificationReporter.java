package pl.training.module8.incremental;

@FunctionalInterface
public interface VerificationReporter {
    void report(VerificationEvent event);

    static VerificationReporter ignoring() {
        return event -> {
        };
    }
}
