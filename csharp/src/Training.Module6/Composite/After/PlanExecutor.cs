namespace Training.Module6.Composite.After;

public sealed class PlanExecutor
{
    public IReadOnlyList<string> Execute(PlanComponent component)
    {
        ArgumentNullException.ThrowIfNull(component);
        var tasks = new List<DeploymentTask>();
        component.CollectTasks(tasks);
        return [.. tasks.Select(task => "executed:" + task.Name)];
    }
}
