export interface DeploymentCostPolicy {
  calculate(baseCostInCents: number): number;
}
