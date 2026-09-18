namespace Training.Module7.MethodObject.After;

public sealed class DeploymentRiskCalculator
{
    public RiskAssessment Calculate(DeploymentRiskInput input) =>
        new DeploymentRiskCalculation(input).Calculate();
}
