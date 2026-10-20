namespace Training.Module5.Observer.After;

/// <summary>
/// Handle returned by <see cref="ReleasePublisher.Subscribe"/>; disposing it
/// unsubscribes (Java: <c>AutoCloseable.close()</c>).
/// </summary>
public interface ISubscription : IDisposable;
