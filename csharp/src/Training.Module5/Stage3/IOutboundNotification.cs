namespace Training.Module5.Stage3;

/// <summary>
/// The client role extracted from the notification hierarchy
/// (Java: <c>@FunctionalInterface OutboundNotification</c>).
/// </summary>
public interface IOutboundNotification
{
    string Dispatch(bool successful);
}
