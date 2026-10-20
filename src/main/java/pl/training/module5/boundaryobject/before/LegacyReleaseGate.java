package pl.training.module5.boundaryobject.before;

import java.util.Map;
import java.util.Objects;

import pl.training.module5.boundaryobject.LegacyCiClient;

/**
 * Usługa domenowa, która pracuje bezpośrednio na surowym ładunku z CI. Parsowanie
 * i nazwy kluczy są rozsiane po metodach, a błędny ładunek wybucha głęboko w domenie
 * (NumberFormatException, NullPointerException).
 */
public final class LegacyReleaseGate {
    private final LegacyCiClient ciClient;

    public LegacyReleaseGate(LegacyCiClient ciClient) {
        this.ciClient = Objects.requireNonNull(ciClient, "ciClient");
    }

    public boolean canRelease(String buildId) {
        Map<String, String> payload = ciClient.fetchBuild(buildId);
        boolean succeeded = "SUCCESS".equals(payload.get("status").trim().toUpperCase());
        int failed = Integer.parseInt(payload.get("failed").trim());
        double coverage = Double.parseDouble(payload.get("coverage").trim());
        return succeeded && failed == 0 && coverage >= 80.0;
    }

    public String summary(String buildId) {
        Map<String, String> payload = ciClient.fetchBuild(buildId);
        return payload.get("id").trim()
                + " [" + payload.get("commit").trim().toLowerCase() + "] "
                + payload.get("status").trim().toUpperCase()
                + " coverage=" + Double.parseDouble(payload.get("coverage").trim()) + "%"
                + " failed=" + Integer.parseInt(payload.get("failed").trim());
    }
}
