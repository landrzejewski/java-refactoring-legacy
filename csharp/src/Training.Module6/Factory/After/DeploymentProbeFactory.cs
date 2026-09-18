namespace Training.Module6.Factory.After;

public sealed class DeploymentProbeFactory
{
    public IDeploymentProbe Create(ProbeKind kind, string? target)
    {
        if (string.IsNullOrWhiteSpace(target))
        {
            var field = kind == ProbeKind.Http ? "endpoint" : "queueName";
            throw new ArgumentException(field + " must not be blank");
        }

        return kind switch
        {
            ProbeKind.Http => new HttpProbe(target),
            ProbeKind.Queue => new QueueProbe(target),
            _ => throw new ArgumentOutOfRangeException(nameof(kind), kind, null)
        };
    }

    public enum ProbeKind
    {
        Http,
        Queue
    }

    private sealed record HttpProbe(string Endpoint) : IDeploymentProbe
    {
        public string Check() => "http-ok:" + Endpoint;
    }

    private sealed record QueueProbe(string QueueName) : IDeploymentProbe
    {
        public string Check() => "queue-ok:" + QueueName;
    }
}
