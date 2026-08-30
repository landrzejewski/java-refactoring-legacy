package pl.training.module7.arrowhead;

public record DeploymentCandidate(
        String releaseId,
        boolean approved,
        boolean testsPassed,
        boolean windowOpen) {
}
