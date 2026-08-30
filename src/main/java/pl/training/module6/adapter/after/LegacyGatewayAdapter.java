package pl.training.module6.adapter.after;

import java.util.Objects;

public final class LegacyGatewayAdapter implements ReleaseNotifier {
    private final LegacyMessageGateway gateway;

    public LegacyGatewayAdapter(LegacyMessageGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    @Override
    public String send(ReleaseMessage message) {
        Objects.requireNonNull(message, "message");
        return gateway.transmit(
                message.recipient(),
                "release:" + message.releaseId());
    }
}
