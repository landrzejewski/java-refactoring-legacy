namespace Training.Module7.MethodObject.After;

internal sealed class DeploymentRiskCalculation
{
    private const long CriticalServicePoints = 20;
    private const long FailedCheckPoints = 10;
    private const long TestedRollbackReduction = 15;

    private readonly DeploymentRiskInput _input;
    private long _score;

    internal DeploymentRiskCalculation(DeploymentRiskInput input)
    {
        ArgumentNullException.ThrowIfNull(input);
        _input = input;
    }

    internal RiskAssessment Calculate()
    {
        AddChangedFilesRisk();
        AddCriticalServicesRisk();
        AddFailedChecksRisk();
        ApplyRollbackReduction();
        LimitScore();
        return new RiskAssessment((int)_score, Classify());
    }

    private void AddChangedFilesRisk() => _score += _input.ChangedFiles;

    private void AddCriticalServicesRisk() =>
        _score += _input.CriticalServices * CriticalServicePoints;

    private void AddFailedChecksRisk() =>
        _score += _input.FailedChecks * FailedCheckPoints;

    private void ApplyRollbackReduction()
    {
        if (_input.RollbackTested)
        {
            _score -= TestedRollbackReduction;
        }
    }

    private void LimitScore() => _score = Math.Clamp(_score, 0, 100);

    private RiskLevel Classify() => _score switch
    {
        < 30 => RiskLevel.Low,
        < 70 => RiskLevel.Medium,
        _ => RiskLevel.High
    };
}
