namespace Training.Module6.Composite.After;

public sealed class PlanBuilder
{
    private readonly string _name;
    private readonly List<PlanComponent> _components = [];
    private bool _built;

    private PlanBuilder(string? name)
    {
        if (string.IsNullOrWhiteSpace(name))
        {
            throw new ArgumentException("name must not be blank");
        }
        _name = name;
    }

    public static PlanBuilder Group(string? name) => new(name);

    public PlanBuilder Task(string? name, long minutes)
    {
        EnsureOpen();
        _components.Add(new DeploymentTask(name, minutes));
        return this;
    }

    public PlanBuilder Group(string? name, Action<PlanBuilder> definition)
    {
        EnsureOpen();
        var child = Group(name);
        ArgumentNullException.ThrowIfNull(definition);
        definition(child);
        var builtChild = child.Build();
        EnsureOpen();
        _components.Add(builtChild);
        return this;
    }

    public PlanBuilder Add(PlanComponent component)
    {
        EnsureOpen();
        ArgumentNullException.ThrowIfNull(component);
        _components.Add(component);
        return this;
    }

    public DeploymentGroup Build()
    {
        EnsureOpen();
        _built = true;
        return new DeploymentGroup(_name, _components);
    }

    private void EnsureOpen()
    {
        if (_built)
        {
            throw new InvalidOperationException("builder has already been used");
        }
    }
}
