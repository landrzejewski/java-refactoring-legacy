namespace Training.Module5.Variability.After;

/// <summary>
/// Odbiorca poleceń operatora.
/// </summary>
public sealed class Cluster
{
    public int Instances { get; private set; } = 2;

    public bool Maintenance { get; private set; }

    internal void AddInstance() => Instances++;

    internal void RemoveInstance()
    {
        if (Instances == 1)
        {
            throw new InvalidOperationException("cannot scale below one instance");
        }
        Instances--;
    }

    internal void ToggleMaintenance() => Maintenance = !Maintenance;

    public override string ToString() =>
        "instances=" + Instances + ", maintenance=" + (Maintenance ? "on" : "off");
}
