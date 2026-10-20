package pl.training.workshop.m5.s31_anticorruptionlayer.step2;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 2: bez zmian. */
public final class SettlementModule {
    private SettlementModule() {
    }

    public static SettlementService settlementService(DistributorFeed feed) {
        return new SettlementService(feed);
    }
}
