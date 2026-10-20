namespace Training.Module5.Variability.Before;

/// <summary>
/// Jedna klasa, trzy różne osie zmienności rozwiązane tym samym narzędziem
/// (<c>switch</c>/<c>if</c>): taryfa klienta, polecenia operatora i cykl życia wydania.
/// </summary>
public sealed class LegacyReleaseConsole
{
    private static readonly HashSet<string> Actions = ["scale-up", "scale-down", "maintenance"];

    private readonly CustomerTier _tier;
    private readonly Queue<string> _queue = new();
    private readonly Stack<string> _history = new();
    private readonly List<string> _log = [];
    private int _instances = 2;
    private bool _maintenance;
    private ReleaseStatus _status = ReleaseStatus.Planned;

    public LegacyReleaseConsole(CustomerTier tier)
    {
        _tier = tier;
    }

    // (a) taryfa jest stała dla klienta, a mimo to wybierana przy każdym wywołaniu
    public long SupportCost(int hours)
    {
        if (hours < 0)
        {
            throw new ArgumentException("hours must not be negative");
        }
        return _tier switch
        {
            CustomerTier.Standard => hours * 100L,
            CustomerTier.Premium => 500 + hours * 80L,
            CustomerTier.Enterprise => Math.Max(2_000, hours * 60L),
            _ => throw new ArgumentOutOfRangeException(nameof(_tier), _tier, "unknown tier")
        };
    }

    // (b) żądanie operatora to tylko nazwa - kolejkowanie, log i cofanie znają ją osobno
    public void Submit(string? action)
    {
        if (action is null || !Actions.Contains(action))
        {
            throw new ArgumentException("unknown action: " + action);
        }
        _queue.Enqueue(action);
    }

    public void RunQueued()
    {
        while (_queue.Count > 0)
        {
            var action = _queue.Dequeue();
            switch (action)
            {
                case "scale-up":
                    _instances++;
                    break;
                case "scale-down":
                    if (_instances == 1)
                    {
                        throw new InvalidOperationException("cannot scale below one instance");
                    }
                    _instances--;
                    break;
                case "maintenance":
                    _maintenance = !_maintenance;
                    break;
                default:
                    throw new ArgumentException("unknown action: " + action);
            }
            _history.Push(action);
            _log.Add(action + " -> " + Cluster());
        }
    }

    public void UndoLast()
    {
        if (_history.Count == 0)
        {
            throw new InvalidOperationException("nothing to undo");
        }
        var action = _history.Pop();
        switch (action)
        {
            case "scale-up":
                _instances--;
                break;
            case "scale-down":
                _instances++;
                break;
            case "maintenance":
                _maintenance = !_maintenance;
                break;
            default:
                throw new ArgumentException("unknown action: " + action);
        }
        _log.Add("undo " + action + " -> " + Cluster());
    }

    public IReadOnlyList<string> Log => [.. _log];

    // (c) zachowanie zależy od statusu, który zmienia się w czasie
    public ReleaseStatus Status => _status;

    public void Advance()
    {
        _status = _status switch
        {
            ReleaseStatus.Planned => ReleaseStatus.Deploying,
            ReleaseStatus.Deploying => ReleaseStatus.Live,
            _ => throw new InvalidOperationException(
                "cannot advance release in state " + _status)
        };
    }

    public void Rollback()
    {
        if (_status == ReleaseStatus.Deploying || _status == ReleaseStatus.Live)
        {
            _status = ReleaseStatus.RolledBack;
            return;
        }
        throw new InvalidOperationException("cannot roll back release in state " + _status);
    }

    public string Banner => _status switch
    {
        ReleaseStatus.Planned => "release planned",
        ReleaseStatus.Deploying => "deployment in progress",
        ReleaseStatus.Live => "release is live",
        ReleaseStatus.RolledBack => "release rolled back",
        _ => throw new ArgumentOutOfRangeException(nameof(_status), _status, "unknown status")
    };

    private string Cluster() =>
        "instances=" + _instances + ", maintenance=" + (_maintenance ? "on" : "off");
}
