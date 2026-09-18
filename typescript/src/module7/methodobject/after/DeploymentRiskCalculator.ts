import type { DeploymentRiskInput } from '../DeploymentRiskInput.js';
import type { RiskAssessment } from '../RiskAssessment.js';
import { DeploymentRiskCalculation } from './DeploymentRiskCalculation.js';

export class DeploymentRiskCalculator {
  calculate(input: DeploymentRiskInput): RiskAssessment {
    return new DeploymentRiskCalculation(input).calculate();
  }
}
