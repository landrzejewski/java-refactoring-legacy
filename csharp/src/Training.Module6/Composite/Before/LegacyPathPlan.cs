namespace Training.Module6.Composite.Before;

public sealed class LegacyPathPlan
{
    private readonly IReadOnlyList<Entry> _entries;

    public LegacyPathPlan(IEnumerable<Entry> entries)
    {
        ArgumentNullException.ThrowIfNull(entries);
        _entries = [.. entries];
    }

    public long TotalMinutes()
    {
        long total = 0;
        foreach (var entry in _entries)
        {
            total = checked(total + entry.Minutes);
        }
        return total;
    }

    public IReadOnlyList<string> TaskNamesBelow(string? path)
    {
        var prefix = RequirePath(path);
        return [.. _entries
            .Where(entry => entry.Path.StartsWith(prefix + "/", StringComparison.Ordinal))
            .Select(entry => entry.TaskName())];
    }

    public sealed record Entry
    {
        public Entry(string? path, long minutes)
        {
            path = RequirePath(path);
            if (!path.Contains('/'))
            {
                throw new ArgumentException("task path must contain a parent");
            }
            if (minutes < 0)
            {
                throw new ArgumentException("minutes must not be negative");
            }
            Path = path;
            Minutes = minutes;
        }

        public string Path { get; }

        public long Minutes { get; }

        internal string TaskName() => Path[(Path.LastIndexOf('/') + 1)..];
    }

    private static string RequirePath(string? path)
    {
        ArgumentNullException.ThrowIfNull(path);
        if (string.IsNullOrWhiteSpace(path) || path.StartsWith('/') || path.EndsWith('/')
            || path.Contains("//", StringComparison.Ordinal))
        {
            throw new ArgumentException("invalid path: " + path);
        }
        foreach (var segment in path.Split('/'))
        {
            if (string.IsNullOrWhiteSpace(segment))
            {
                throw new ArgumentException("invalid path: " + path);
            }
        }
        return path;
    }
}
