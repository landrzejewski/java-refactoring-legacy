namespace Training.Module6.Factory.Before;

public sealed record QueueProbe : IDeploymentProbe
{
    public QueueProbe(string? queueName)
    {
        if (string.IsNullOrWhiteSpace(queueName))
        {
            throw new ArgumentException("queueName must not be blank");
        }
        QueueName = queueName;
    }

    public string QueueName { get; }

    public string Check() => "queue-ok:" + QueueName;
}
