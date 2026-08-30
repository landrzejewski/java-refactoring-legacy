package pl.training.module7.methodobject.before;

import java.util.Objects;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;
import pl.training.module7.methodobject.RiskLevel;

public final class LegacyDeploymentRiskCalculator {
    public RiskAssessment calculate(DeploymentRiskInput input) {
        Objects.requireNonNull(input, "input");

        long score = input.changedFiles();
        score += (long) input.criticalServices() * 20;
        score += (long) input.failedChecks() * 10;

        if (input.rollbackTested()) {
            score -= 15;
        }

        score = Math.max(0, Math.min(100, score));

        RiskLevel level;
        if (score < 30) {
            level = RiskLevel.LOW;
        } else if (score < 70) {
            level = RiskLevel.MEDIUM;
        } else {
            level = RiskLevel.HIGH;
        }

        return new RiskAssessment((int) score, level);
    }
}
