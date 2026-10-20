namespace Training.Module5.Variability.After;

/// <summary>
/// Invoker: kolejkuje, wykonuje, loguje i cofa polecenia, nie znając ich treści.
/// </summary>
public sealed class CommandConsole
{
    private readonly Queue<OperatorCommand> _queue = new();
    private readonly Stack<OperatorCommand> _history = new();
    private readonly List<string> _log = [];

    public Cluster Cluster { get; } = new();

    public void Submit(OperatorCommand command)
    {
        ArgumentNullException.ThrowIfNull(command);
        _queue.Enqueue(command);
    }

    public void RunQueued()
    {
        while (_queue.Count > 0)
        {
            var command = _queue.Dequeue();
            command.Execute(Cluster);
            _history.Push(command);
            _log.Add(command.Name + " -> " + Cluster);
        }
    }

    public void UndoLast()
    {
        if (_history.Count == 0)
        {
            throw new InvalidOperationException("nothing to undo");
        }
        var command = _history.Pop();
        command.Undo(Cluster);
        _log.Add("undo " + command.Name + " -> " + Cluster);
    }

    public IReadOnlyList<string> Log => [.. _log];
}
