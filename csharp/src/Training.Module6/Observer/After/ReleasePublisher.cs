using System.Collections.Immutable;

namespace Training.Module6.Observer.After;

public sealed class ReleasePublisher
{
    // Copy-on-write list: every publication iterates an immutable snapshot.
    private ImmutableList<Registration> _listeners = [];

    public ISubscription Subscribe(ReleaseListener listener)
    {
        ArgumentNullException.ThrowIfNull(listener);
        var registration = new Registration(listener);
        ImmutableInterlocked.Update(ref _listeners, list => list.Add(registration));
        return new ActiveSubscription(this, registration);
    }

    public void Publish(ReleasePublished @event)
    {
        ArgumentNullException.ThrowIfNull(@event);
        foreach (var registration in Volatile.Read(ref _listeners))
        {
            registration.NotifyListener(@event);
        }
    }

    private void Remove(Registration registration) =>
        ImmutableInterlocked.Update(ref _listeners, list => list.Remove(registration));

    private sealed class Registration(ReleaseListener listener)
    {
        public void NotifyListener(ReleasePublished @event) => listener(@event);
    }

    private sealed class ActiveSubscription(
        ReleasePublisher publisher,
        Registration registration) : ISubscription
    {
        private int _active = 1;

        public void Dispose()
        {
            if (Interlocked.CompareExchange(ref _active, 0, 1) == 1)
            {
                publisher.Remove(registration);
            }
        }
    }
}
