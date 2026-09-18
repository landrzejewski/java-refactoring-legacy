namespace Training.Module6.Adapter.After;

public sealed class LegacyGatewayAdapter : IReleaseNotifier
{
    private readonly LegacyMessageGateway _gateway;

    public LegacyGatewayAdapter(LegacyMessageGateway gateway)
    {
        ArgumentNullException.ThrowIfNull(gateway);
        _gateway = gateway;
    }

    public string Send(ReleaseMessage message)
    {
        ArgumentNullException.ThrowIfNull(message);
        return _gateway(message.Recipient, "release:" + message.ReleaseId);
    }
}
