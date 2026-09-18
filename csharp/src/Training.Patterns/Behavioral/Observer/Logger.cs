namespace Training.Patterns.Behavioral.Observer;

/// <summary>Java: <c>implements Consumer&lt;ServerEvent&gt;</c> - here the method group <c>Accept</c> is used as an <see cref="Action{T}"/>.</summary>
public class Logger
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Logger));

    internal void Accept(ServerEvent serverEvent)
    {
        log.Info(serverEvent.Payload);
    }
}
