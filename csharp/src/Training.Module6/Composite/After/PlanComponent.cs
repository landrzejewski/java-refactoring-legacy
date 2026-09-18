namespace Training.Module6.Composite.After;

/// <summary>
/// Closed component hierarchy: only <see cref="DeploymentTask"/> and
/// <see cref="DeploymentGroup"/> can derive from it (Java: sealed interface ... permits).
/// </summary>
public abstract record PlanComponent
{
    private protected PlanComponent()
    {
    }

    public abstract string Name { get; }

    public abstract long TotalMinutes();

    public abstract void CollectTasks(ICollection<DeploymentTask> target);

    public abstract TResult Accept<TResult>(IPlanVisitor<TResult> visitor);
}
