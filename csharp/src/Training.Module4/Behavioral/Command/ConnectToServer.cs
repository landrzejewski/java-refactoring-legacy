namespace Training.Module4.Behavioral.Command;

public class ConnectToServer : ICommand
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(ConnectToServer));

    public void Execute()
    {
        log.Info("Connecting...");
        log.Info("Connected...");
    }
}
