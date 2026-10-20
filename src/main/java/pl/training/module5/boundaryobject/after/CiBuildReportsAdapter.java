package pl.training.module5.boundaryobject.after;

import java.util.Objects;

import pl.training.module5.boundaryobject.LegacyCiClient;

/**
 * Adapter na granicy: implementuje port domenowy, woła zewnętrzny klient i od razu
 * zamienia ładunek na obiekt graniczny.
 */
public final class CiBuildReportsAdapter implements BuildReports {
    private final LegacyCiClient ciClient;

    public CiBuildReportsAdapter(LegacyCiClient ciClient) {
        this.ciClient = Objects.requireNonNull(ciClient, "ciClient");
    }

    @Override
    public BuildReport find(String buildId) {
        return BuildReportMapper.fromPayload(ciClient.fetchBuild(buildId));
    }
}
