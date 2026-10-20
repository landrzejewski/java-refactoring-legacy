namespace Training.Module5.Facade.Before;

/// <summary>
/// Drugi klient z tą samą orkiestracją, skopiowaną i nieco inaczej zapisaną.
/// Zmiana kolejności kroków w jednym miejscu łatwo rozjedzie się z drugim.
/// </summary>
public sealed class LegacyReleaseScheduler
{
    private readonly ArtifactStore _store;
    private readonly ConfigRenderer _renderer;
    private readonly DeploymentQueue _queue;
    private readonly ReleaseNotifier _notifier;

    public LegacyReleaseScheduler(
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

    public IReadOnlyList<string> PublishNightly(IEnumerable<string?> releaseIds)
    {
        var published = new List<string>();
        foreach (var releaseId in releaseIds)
        {
            if (string.IsNullOrWhiteSpace(releaseId))
            {
                throw new ArgumentException("releaseId must not be blank");
            }
            var artifact = _store.Upload(releaseId);
            var config = _renderer.Render(releaseId, "staging");
            _queue.Enqueue(artifact, config);
            _notifier.Announce(releaseId, "staging");
            published.Add("published " + releaseId + " to staging");
        }
        return published;
    }
}
