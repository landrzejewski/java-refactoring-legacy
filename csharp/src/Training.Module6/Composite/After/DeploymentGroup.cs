namespace Training.Module6.Composite.After;

public sealed record DeploymentGroup : PlanComponent
{
    public DeploymentGroup(string? name, IEnumerable<PlanComponent> components)
    {
        if (string.IsNullOrWhiteSpace(name))
        {
            throw new ArgumentException("name must not be blank");
        }
        ArgumentNullException.ThrowIfNull(components);
        Name = name;
        Components = [.. components];
    }

    public override string Name { get; }

    public IReadOnlyList<PlanComponent> Components { get; }

    public override long TotalMinutes()
    {
        long total = 0;
        foreach (var component in Components)
        {
            total = checked(total + component.TotalMinutes());
        }
        return total;
    }

    public override void CollectTasks(ICollection<DeploymentTask> target)
    {
        ArgumentNullException.ThrowIfNull(target);
        foreach (var component in Components)
        {
            component.CollectTasks(target);
        }
    }

    public override TResult Accept<TResult>(IPlanVisitor<TResult> visitor)
    {
        ArgumentNullException.ThrowIfNull(visitor);
        return visitor.VisitGroup(this);
    }

    // Java records compare List components structurally; C# records compare
    // collections by reference, so value equality is spelled out explicitly.
    public bool Equals(DeploymentGroup? other) =>
        other is not null
        && Name == other.Name
        && Components.SequenceEqual(other.Components);

    public override int GetHashCode()
    {
        var hash = new HashCode();
        hash.Add(Name);
        foreach (var component in Components)
        {
            hash.Add(component);
        }
        return hash.ToHashCode();
    }
}
