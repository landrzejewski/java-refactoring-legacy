namespace Training.Module6.Composite.Before;

public sealed class LegacyPlanExecutor
{
    public IReadOnlyList<string> Execute(Task task) => ["executed:" + task.Name];

    public IReadOnlyList<string> ExecuteAll(IEnumerable<Task> tasks) =>
        [.. tasks.Select(task => "executed:" + task.Name)];

    public sealed record Task
    {
        public Task(string? name)
        {
            if (string.IsNullOrWhiteSpace(name))
            {
                throw new ArgumentException("name must not be blank");
            }
            Name = name;
        }

        public string Name { get; }
    }
}
