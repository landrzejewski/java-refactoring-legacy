namespace Training.Module3.BreakDependencies.After;

public interface IMaintenanceWindows
{
    bool Allows(string service, int hourUtc);
}
