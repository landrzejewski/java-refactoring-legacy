package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module6.adapter.after.LegacyGatewayAdapter;
import pl.training.module6.adapter.after.NotificationService;
import pl.training.module6.adapter.after.ReleaseMessage;
import pl.training.module6.adapter.before.LegacyNotificationClient;

final class AdapterEquivalenceTest {
    @Test
    void adapterMapsThePreferredContractToTheLegacyProtocol() {
        var before = new LegacyNotificationClient();
        LegacyNotificationClient.LegacyMessageGateway legacyGateway =
                (destination, body) -> destination + "|" + body;
        String legacyResult = before.notifyUsingLegacy(
                legacyGateway, "ops", "rel-42");

        var service = new NotificationService(new LegacyGatewayAdapter(
                (destination, body) -> destination + "|" + body));

        assertEquals(
                legacyResult,
                service.notify(new ReleaseMessage("ops", "rel-42")));
    }

    @Test
    void clientCanUseNativeAndAdaptedImplementationsThroughOneInterface() {
        ReleaseMessage message = new ReleaseMessage("ops", "rel-42");
        var nativeService = new NotificationService(
                value -> value.recipient() + "|release:" + value.releaseId());
        var adaptedService = new NotificationService(new LegacyGatewayAdapter(
                (destination, body) -> destination + "|" + body));

        assertEquals(nativeService.notify(message), adaptedService.notify(message));
    }

    @Test
    void messageObjectPreservesLegacyValidation() {
        var before = new LegacyNotificationClient();

        RuntimeException legacyFailure = assertThrows(
                RuntimeException.class,
                () -> before.notifyUsingLegacy(
                        (destination, body) -> body, " ", "rel-42"));
        RuntimeException refactoredFailure = assertThrows(
                RuntimeException.class,
                () -> new ReleaseMessage(" ", "rel-42"));

        assertEquals(legacyFailure.getClass(), refactoredFailure.getClass());
        assertEquals(legacyFailure.getMessage(), refactoredFailure.getMessage());
    }
}
