namespace Training.Module7.MethodObject.Before;

public sealed class LegacyDeploymentRiskCalculator
{
    public RiskAssessment Calculate(DeploymentRiskInput input)
    {
        ArgumentNullException.ThrowIfNull(input);

        long score = input.ChangedFiles;
        score += (long)input.CriticalServices * 20;
        score += (long)input.FailedChecks * 10;

        if (input.RollbackTested)
        {
            score -= 15;
        }

        score = Math.Max(0, Math.Min(100, score));

        RiskLevel level;
        if (score < 30)
        {
            level = RiskLevel.Low;
        }
        else if (score < 70)
        {
            level = RiskLevel.Medium;
        }
        else
        {
            level = RiskLevel.High;
        }

        return new RiskAssessment((int)score, level);
    }
}
