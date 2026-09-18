namespace Training.Patterns.Behavioral.Observer;

internal class EventsBus
{
    // Java: Collections.synchronizedSet(new HashSet<>()) -> HashSet guarded by a lock
    private readonly HashSet<Action<ServerEvent>> consumers = [];
    private readonly Lock consumersLock = new();

    internal void AddConsumer(Action<ServerEvent> consumer)
    {
        lock (consumersLock)
        {
            consumers.Add(consumer);
        }
    }

    internal void Publish(ServerEvent @event)
    {
        lock (consumersLock)
        {
            foreach (var consumer in consumers)
            {
                consumer(@event);
            }
        }
    }
}
