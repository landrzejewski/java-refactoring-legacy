namespace Training.Module6.Composite.After;

public sealed class TotalMinutesVisitor : IPlanVisitor<long>
{
    public long VisitTask(DeploymentTask task) => task.Minutes;

    public long VisitGroup(DeploymentGroup group)
    {
        long total = 0;
        foreach (var component in group.Components)
        {
            total = checked(total + component.Accept(this));
        }
        return total;
    }
}
