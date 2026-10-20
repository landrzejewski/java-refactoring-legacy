package pl.training.workshop.m5.s31_anticorruptionlayer.start;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Start: serwis dostaje klienta dystrybutora wprost. */
public final class SettlementModule {
    private SettlementModule() {
    }

    public static SettlementService settlementService(DistributorFeed feed) {
        return new SettlementService(feed);
    }
}
