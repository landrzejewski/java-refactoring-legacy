using Training.Module6.Composite.Before;

namespace Training.Module6.Composite.After;

public sealed class LegacyPathPlanMapper
{
    public DeploymentGroup Map(
        string? rootName,
        IEnumerable<LegacyPathPlan.Entry> entries)
    {
        RequireRootName(rootName);
        ArgumentNullException.ThrowIfNull(entries);
        List<LegacyPathPlan.Entry> source = [.. entries];
        ValidateDepthFirstOrder(rootName!, source);

        var root = new GroupNode(rootName!);
        foreach (var entry in source)
        {
            var segments = SegmentsBelow(rootName!, entry);
            var parent = root;
            for (var index = 1; index < segments.Length - 1; index++)
            {
                parent = parent.Group(segments[index], entry.Path);
            }
            parent.Task(segments[^1], entry.Minutes, entry.Path);
        }
        return root.Freeze();
    }

    private static void ValidateDepthFirstOrder(
        string rootName,
        IReadOnlyList<LegacyPathPlan.Entry> entries)
    {
        IReadOnlyList<string> previousParents = [];
        var closedGroups = new HashSet<string>();
        foreach (var entry in entries)
        {
            var segments = SegmentsBelow(rootName, entry);
            var currentParents = ParentPaths(segments);
            var common = CommonPrefixLength(previousParents, currentParents);
            closedGroups.UnionWith(previousParents.Skip(common));
            foreach (var parent in currentParents)
            {
                if (closedGroups.Contains(parent))
                {
                    throw new ArgumentException(
                        "entries are not in depth-first order: " + entry.Path);
                }
            }
            previousParents = currentParents;
        }
    }

    private static string[] SegmentsBelow(
        string rootName,
        LegacyPathPlan.Entry? entry)
    {
        if (entry is null)
        {
            throw new ArgumentNullException(
                nameof(entry), "entries must not contain null");
        }
        var segments = entry.Path.Split('/');
        if (segments[0] != rootName)
        {
            throw new ArgumentException(
                "entry is outside root " + rootName + ": " + entry.Path);
        }
        foreach (var segment in segments)
        {
            if (string.IsNullOrWhiteSpace(segment))
            {
                throw new ArgumentException(
                    "path segment must not be blank: " + entry.Path);
            }
        }
        return segments;
    }

    private static IReadOnlyList<string> ParentPaths(string[] segments)
    {
        var parents = new List<string>();
        var path = new System.Text.StringBuilder(segments[0]);
        for (var index = 1; index < segments.Length - 1; index++)
        {
            path.Append('/').Append(segments[index]);
            parents.Add(path.ToString());
        }
        return parents.AsReadOnly();
    }

    private static int CommonPrefixLength(
        IReadOnlyList<string> first,
        IReadOnlyList<string> second)
    {
        var length = Math.Min(first.Count, second.Count);
        var index = 0;
        while (index < length && first[index] == second[index])
        {
            index++;
        }
        return index;
    }

    private static void RequireRootName(string? rootName)
    {
        if (string.IsNullOrWhiteSpace(rootName) || rootName.Contains('/'))
        {
            throw new ArgumentException(
                "rootName must be one non-blank path segment");
        }
    }

    private interface INode
    {
        string Name { get; }

        PlanComponent Freeze();
    }

    private sealed class GroupNode(string name) : INode
    {
        private readonly List<INode> _children = [];
        private readonly Dictionary<string, GroupNode> _groups = [];
        private readonly HashSet<string> _taskNames = [];

        public string Name { get; } = name;

        public GroupNode Group(string childName, string sourcePath)
        {
            if (_taskNames.Contains(childName))
            {
                throw PathConflict(sourcePath, childName);
            }
            if (_groups.TryGetValue(childName, out var existing))
            {
                return existing;
            }
            var created = new GroupNode(childName);
            _groups.Add(childName, created);
            _children.Add(created);
            return created;
        }

        public void Task(string taskName, long minutes, string sourcePath)
        {
            if (_groups.ContainsKey(taskName))
            {
                throw PathConflict(sourcePath, taskName);
            }
            _taskNames.Add(taskName);
            _children.Add(new TaskNode(taskName, minutes));
        }

        private static ArgumentException PathConflict(
            string sourcePath,
            string childName) =>
            new("path is both a task and a group at "
                + childName + ": " + sourcePath);

        public DeploymentGroup Freeze() =>
            new(Name, _children.Select(child => child.Freeze()));

        PlanComponent INode.Freeze() => Freeze();
    }

    private sealed record TaskNode(string Name, long Minutes) : INode
    {
        public DeploymentTask Freeze() => new(Name, Minutes);

        PlanComponent INode.Freeze() => Freeze();
    }
}
