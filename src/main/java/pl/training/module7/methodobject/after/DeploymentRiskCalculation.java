package pl.training.module7.methodobject.after;

import java.util.Objects;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;
import pl.training.module7.methodobject.RiskLevel;

final class DeploymentRiskCalculation {
    private static final long CRITICAL_SERVICE_POINTS = 20;
    private static final long FAILED_CHECK_POINTS = 10;
    private static final long TESTED_ROLLBACK_REDUCTION = 15;

    private final DeploymentRiskInput input;
    private long score;

    DeploymentRiskCalculation(DeploymentRiskInput input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    RiskAssessment calculate() {
        addChangedFilesRisk();
        addCriticalServicesRisk();
        addFailedChecksRisk();
        applyRollbackReduction();
        limitScore();
        return new RiskAssessment((int) score, classify());
    }

    private void addChangedFilesRisk() {
        score += input.changedFiles();
    }

    private void addCriticalServicesRisk() {
        score += (long) input.criticalServices() * CRITICAL_SERVICE_POINTS;
    }

    private void addFailedChecksRisk() {
        score += (long) input.failedChecks() * FAILED_CHECK_POINTS;
    }

    private void applyRollbackReduction() {
        if (input.rollbackTested()) {
            score -= TESTED_ROLLBACK_REDUCTION;
        }
    }

    private void limitScore() {
        score = Math.max(0, Math.min(100, score));
    }

    private RiskLevel classify() {
        if (score < 30) {
            return RiskLevel.LOW;
        }
        if (score < 70) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.HIGH;
    }
}
