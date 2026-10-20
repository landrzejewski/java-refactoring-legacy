namespace Training.Module5.Facade.Before;

/// <summary>
/// Klient sam orkiestruje cztery podsystemy i sam pilnuje kolejności oraz walidacji.
/// Ta sama wiedza jest skopiowana w <see cref="LegacyReleaseScheduler"/>.
/// </summary>
public sealed class LegacyReleaseCli
{
    private readonly ArtifactStore _store;
    private readonly ConfigRenderer _renderer;
    private readonly DeploymentQueue _queue;
    private readonly ReleaseNotifier _notifier;

    public LegacyReleaseCli(
        ArtifactStore store,
        ConfigRenderer renderer,
        DeploymentQueue queue,
        ReleaseNotifier notifier)
    {
        ArgumentNullException.ThrowIfNull(store);
        ArgumentNullException.ThrowIfNull(renderer);
        ArgumentNullException.ThrowIfNull(queue);
        ArgumentNullException.ThrowIfNull(notifier);
        _store = store;
        _renderer = renderer;
        _queue = queue;
        _notifier = notifier;
    }

    public string Publish(string? releaseId, string environment)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        var target = environment.Trim().ToLowerInvariant();
        if (target is not ("staging" or "prod"))
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
