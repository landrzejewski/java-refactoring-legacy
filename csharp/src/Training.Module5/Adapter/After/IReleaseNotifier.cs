namespace Training.Module5.Adapter.After;

public interface IReleaseNotifier
{
    string Send(ReleaseMessage message);
}
