namespace Training.Module5.Facade.After;

/// <summary>
/// Fasada jako strażnik spójności: jedna operacja wymusza kolejność kroków i walidację.
/// Podsystemy są wewnętrzne (Java: pakietowo-prywatne, C#: <c>internal</c>), więc klient
/// spoza modułu nie może ich złożyć w złej kolejności ani pominąć kroku.
/// </summary>
public sealed class ReleasePublishingFacade
{
    private static readonly HashSet<string> Environments = ["staging", "prod"];

    private readonly ArtifactStore _store;
    private readonly ConfigRenderer _renderer;
    private readonly DeploymentQueue _queue;
    private readonly ReleaseNotifier _notifier;

    public ReleasePublishingFacade(Action<string> effects)
    {
        _store = new ArtifactStore(effects);
        _renderer = new ConfigRenderer(effects);
        _queue = new DeploymentQueue(effects);
        _notifier = new ReleaseNotifier(effects);
    }

    public string Publish(string? releaseId, string environment)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        ArgumentNullException.ThrowIfNull(environment);
        var target = environment.Trim().ToLowerInvariant();
        if (!Environments.Contains(target))
        {
            throw new ArgumentException("unknown environment: " + environment);
        }
        var artifact = _store.Upload(releaseId);
        var config = _renderer.Render(releaseId, target);
        _queue.Enqueue(artifact, config);
        _notifier.Announce(releaseId, target);
        return "published " + releaseId + " to " + target;
    }
}
