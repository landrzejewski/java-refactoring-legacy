using Training.Module6.Adapter.After;
using Training.Module6.Adapter.Before;

namespace Training.Module6.Tests;

public sealed class AdapterEquivalenceTest
{
    [Fact]
    public void AdapterMapsThePreferredContractToTheLegacyProtocol()
    {
        var before = new LegacyNotificationClient();
        LegacyNotificationClient.LegacyMessageGateway legacyGateway =
            (destination, body) => destination + "|" + body;
        var legacyResult = before.NotifyUsingLegacy(legacyGateway, "ops", "rel-42");

        var service = new NotificationService(new LegacyGatewayAdapter(
            (destination, body) => destination + "|" + body));

        Assert.Equal(legacyResult, service.Notify(new ReleaseMessage("ops", "rel-42")));
    }

    [Fact]
    public void ClientCanUseNativeAndAdaptedImplementationsThroughOneInterface()
    {
        var message = new ReleaseMessage("ops", "rel-42");
        var nativeService = new NotificationService(new NativeNotifier());
        var adaptedService = new NotificationService(new LegacyGatewayAdapter(
            (destination, body) => destination + "|" + body));

        Assert.Equal(nativeService.Notify(message), adaptedService.Notify(message));
    }

    [Fact]
    public void MessageObjectPreservesLegacyValidation()
    {
        var before = new LegacyNotificationClient();

        var legacyFailure = Assert.ThrowsAny<Exception>(
            () => before.NotifyUsingLegacy((destination, body) => body, " ", "rel-42"));
        var refactoredFailure = Assert.ThrowsAny<Exception>(
            () => new ReleaseMessage(" ", "rel-42"));

        Assert.Equal(legacyFailure.GetType(), refactoredFailure.GetType());
        Assert.Equal(legacyFailure.Message, refactoredFailure.Message);
    }

    // Java passes a lambda; a C# lambda cannot implement an interface.
    private sealed class NativeNotifier : IReleaseNotifier
    {
        public string Send(ReleaseMessage message) =>
            message.Recipient + "|release:" + message.ReleaseId;
    }
}
