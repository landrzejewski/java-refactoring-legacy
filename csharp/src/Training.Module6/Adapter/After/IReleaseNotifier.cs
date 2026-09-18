namespace Training.Module6.Adapter.After;

public interface IReleaseNotifier
{
    string Send(ReleaseMessage message);
}
