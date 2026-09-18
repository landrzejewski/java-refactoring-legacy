namespace Training.Module7.BreakDependencies.Before;

public sealed class StandardMaintenanceWindows
{
    private const int WindowStartHourUtc = 0;
    private const int WindowEndHourUtc = 6;

    public bool Allows(string service, int hourUtc) =>
        hourUtc >= WindowStartHourUtc
        && hourUtc < WindowEndHourUtc;
}
