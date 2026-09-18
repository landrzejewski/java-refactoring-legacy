namespace Training.Module6.Polymorphism.After;

/// <summary>
/// Closed set of step variants (Java: sealed interface ... permits ScriptStep, ApprovalStep).
/// </summary>
public abstract record DeploymentStep
{
    private protected DeploymentStep()
    {
    }

    public abstract string Execute();
}
