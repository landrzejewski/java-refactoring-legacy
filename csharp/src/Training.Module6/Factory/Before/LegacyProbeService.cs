namespace Training.Module6.Factory.Before;

public sealed class LegacyProbeService
{
    public string Check(ProbeKind kind, string? target)
    {
        IDeploymentProbe probe = kind switch
        {
            ProbeKind.Http => new HttpProbe(target),
            ProbeKind.Queue => new QueueProbe(target),
            _ => throw new ArgumentOutOfRangeException(nameof(kind), kind, null)
        };
        return probe.Check();
    }

    public enum ProbeKind
    {
        Http,
        Queue
    }
}
