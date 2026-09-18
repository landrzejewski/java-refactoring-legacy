namespace Training.Module7.BreakDependencies.After;

public interface IMaintenanceWindows
{
    bool Allows(string service, int hourUtc);
}
