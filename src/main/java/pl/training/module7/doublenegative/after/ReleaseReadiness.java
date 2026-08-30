package pl.training.module7.doublenegative.after;

public record ReleaseReadiness(
        boolean approved,
        boolean testsPassed,
        boolean windowOpen) {
}
