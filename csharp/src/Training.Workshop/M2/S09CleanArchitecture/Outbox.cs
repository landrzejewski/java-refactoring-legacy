namespace Training.Workshop.M2.S09CleanArchitecture;

/// <summary>Świat zewnętrzny sceny (stabilny): kanał komunikatów (np. broker) - temat i treść.</summary>
public sealed class Outbox
{
    private readonly List<string> _messages = [];

    public void Publish(string topic, string payload)
    {
        _messages.Add(topic + ":" + payload);
    }

    public IReadOnlyList<string> Messages => _messages.ToList();
}
