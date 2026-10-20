namespace Training.Module5.Composite.After;

public interface IPlanVisitor<TResult>
{
    TResult VisitTask(DeploymentTask task);

    TResult VisitGroup(DeploymentGroup group);
}
