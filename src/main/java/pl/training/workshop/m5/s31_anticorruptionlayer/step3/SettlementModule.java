package pl.training.workshop.m5.s31_anticorruptionlayer.step3;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: domena dostaje licencje przez ACL. */
public final class SettlementModule {
    private SettlementModule() {
    }

    public static SettlementService settlementService(DistributorFeed feed) {
        return new SettlementService(new DistributorAcl(feed));
    }
}
