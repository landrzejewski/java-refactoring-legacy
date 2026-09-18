namespace Training.Patterns.Behavioral.Command;

public class Invoker
{
    private readonly Queue<ICommand> commands = new();

    public void Register(ICommand command)
    {
        commands.Enqueue(command);
    }

    public void InvokeAll()
    {
        foreach (var command in commands)
        {
            command.Execute();
        }
    }

    public void Invoke(ICommand command)
    {
        command.Execute();
    }
}
