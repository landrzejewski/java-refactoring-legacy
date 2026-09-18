namespace Training.Module6.ExtractComposite.Before;

public static class LegacyPlanNodes
{
    public interface IPlanNode
    {
        long TotalMinutes();
    }

    public sealed record TaskNode : IPlanNode
    {
        public TaskNode(long minutes)
        {
            if (minutes < 0)
            {
                throw new ArgumentException("minutes must not be negative");
            }
            Minutes = minutes;
        }

        public long Minutes { get; }

        public long TotalMinutes() => Minutes;
    }

    public sealed class ReleaseGroup : IPlanNode
    {
        private readonly List<IPlanNode> _children = [];

        public void Add(IPlanNode child)
        {
            ArgumentNullException.ThrowIfNull(child);
            _children.Add(child);
        }

        public IReadOnlyList<IPlanNode> Children() => [.. _children];

        public long TotalMinutes()
        {
            long total = 0;
            foreach (var child in _children)
            {
                total = checked(total + child.TotalMinutes());
            }
            return total;
        }
    }

    public sealed class RollbackGroup : IPlanNode
    {
        private readonly List<IPlanNode> _children = [];

        public void Add(IPlanNode child)
        {
            ArgumentNullException.ThrowIfNull(child);
            _children.Add(child);
        }

        public IReadOnlyList<IPlanNode> Children() => [.. _children];

        public long TotalMinutes()
        {
            long total = 0;
            foreach (var child in _children)
            {
                total = checked(total + child.TotalMinutes());
            }
            return total;
        }
    }
}
