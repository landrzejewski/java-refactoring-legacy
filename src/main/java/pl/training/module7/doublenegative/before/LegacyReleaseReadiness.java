package pl.training.module7.doublenegative.before;

public record LegacyReleaseReadiness(
        boolean notApproved,
        boolean testsNotPassed,
        boolean windowNotOpen) {
}
