import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentRiskInput } from '../DeploymentRiskInput.js';
import { RiskAssessment } from '../RiskAssessment.js';
import { RiskLevel } from '../RiskLevel.js';

// W Javie klasa pakietowa (package-private). TS nie ma takiej widoczności —
// eksport jest potrzebny fasadzie DeploymentRiskCalculator; to szczegół
// implementacji, nie publiczne API przykładu.
export class DeploymentRiskCalculation {
  private static readonly CRITICAL_SERVICE_POINTS = 20;
  private static readonly FAILED_CHECK_POINTS = 10;
  private static readonly TESTED_ROLLBACK_REDUCTION = 15;

  private readonly input: DeploymentRiskInput;
  private score = 0;

  constructor(input: DeploymentRiskInput) {
    this.input = requireNonNull(input, 'input');
  }

  calculate(): RiskAssessment {
    this.addChangedFilesRisk();
    this.addCriticalServicesRisk();
    this.addFailedChecksRisk();
    this.applyRollbackReduction();
    this.limitScore();
    return new RiskAssessment(this.score, this.classify());
  }

  private addChangedFilesRisk(): void {
    this.score += this.input.changedFiles;
  }

  private addCriticalServicesRisk(): void {
    this.score += this.input.criticalServices
      * DeploymentRiskCalculation.CRITICAL_SERVICE_POINTS;
  }

  private addFailedChecksRisk(): void {
    this.score += this.input.failedChecks
      * DeploymentRiskCalculation.FAILED_CHECK_POINTS;
  }

  private applyRollbackReduction(): void {
    if (this.input.rollbackTested) {
      this.score -= DeploymentRiskCalculation.TESTED_ROLLBACK_REDUCTION;
    }
  }

  private limitScore(): void {
    this.score = Math.max(0, Math.min(100, this.score));
  }

  private classify(): RiskLevel {
    if (this.score < 30) {
      return RiskLevel.LOW;
    }
    if (this.score < 70) {
      return RiskLevel.MEDIUM;
    }
    return RiskLevel.HIGH;
  }
}
