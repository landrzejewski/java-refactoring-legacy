namespace Training.Module4.Behavioral.Command;

public class PrintTime : ICommand
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(PrintTime));

    public void Execute()
    {
        log.Info(JavaText.Of(DateTime.Now)); // Java: LocalDateTime.now().toString()
    }
}
