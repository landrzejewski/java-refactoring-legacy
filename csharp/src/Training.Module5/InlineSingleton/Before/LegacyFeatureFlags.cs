namespace Training.Module5.InlineSingleton.Before;

/// <summary>
/// Globalny, mutowalny singleton. Każdy może go zmienić z dowolnego miejsca, a klasy,
/// które z niego korzystają, nie pokazują tej zależności w konstruktorze.
/// </summary>
public sealed class LegacyFeatureFlags
{
    private readonly HashSet<string> _enabled = [];
    private readonly Lock _lock = new();

    private LegacyFeatureFlags()
    {
    }

    public static LegacyFeatureFlags Instance { get; } = new();

    public void Enable(string flag)
    {
        lock (_lock)
        {
            _enabled.Add(flag);
        }
    }

    public void Disable(string flag)
    {
        lock (_lock)
        {
            _enabled.Remove(flag);
        }
    }

    public bool IsEnabled(string flag)
    {
        lock (_lock)
        {
            return _enabled.Contains(flag);
        }
    }

    // istnieje tylko po to, żeby testy mogły posprzątać globalny stan
    public void Reset()
    {
        lock (_lock)
        {
            _enabled.Clear();
        }
    }
}
