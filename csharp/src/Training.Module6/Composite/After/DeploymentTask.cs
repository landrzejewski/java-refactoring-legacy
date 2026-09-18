namespace Training.Module6.Composite.After;

public sealed record DeploymentTask : PlanComponent
{
    public DeploymentTask(string? name, long minutes)
    {
        if (string.IsNullOrWhiteSpace(name))
        {
            throw new ArgumentException("name must not be blank");
        }
        if (minutes < 0)
        {
            throw new ArgumentException("minutes must not be negative");
        }
        Name = name;
        Minutes = minutes;
    }

    public override string Name { get; }

    public long Minutes { get; }

    public override long TotalMinutes() => Minutes;

    public override void CollectTasks(ICollection<DeploymentTask> target)
    {
        ArgumentNullException.ThrowIfNull(target);
        target.Add(this);
    }

    public override TResult Accept<TResult>(IPlanVisitor<TResult> visitor)
    {
        ArgumentNullException.ThrowIfNull(visitor);
        return visitor.VisitTask(this);
    }
}
