package pl.training.module7.methodobject.after;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;

public final class DeploymentRiskCalculator {
    public RiskAssessment calculate(DeploymentRiskInput input) {
        return new DeploymentRiskCalculation(input).calculate();
    }
}
