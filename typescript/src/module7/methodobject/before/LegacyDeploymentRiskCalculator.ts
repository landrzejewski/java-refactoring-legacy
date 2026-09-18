import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentRiskInput } from '../DeploymentRiskInput.js';
import { RiskAssessment } from '../RiskAssessment.js';
import { RiskLevel } from '../RiskLevel.js';

export class LegacyDeploymentRiskCalculator {
  calculate(input: DeploymentRiskInput): RiskAssessment {
    requireNonNull(input, 'input');

    let score = input.changedFiles;
    score += input.criticalServices * 20;
    score += input.failedChecks * 10;

    if (input.rollbackTested) {
      score -= 15;
    }

    score = Math.max(0, Math.min(100, score));

    let level: RiskLevel;
    if (score < 30) {
      level = RiskLevel.LOW;
    } else if (score < 70) {
      level = RiskLevel.MEDIUM;
    } else {
      level = RiskLevel.HIGH;
    }

    return new RiskAssessment(score, level);
  }
}
