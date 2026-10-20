namespace Training.Workshop.M5.S31AntiCorruptionLayer.Start;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji). Start: serwis dostaje klienta dystrybutora wprost.</summary>
public static class SettlementModule
{
    public static SettlementService SettlementService(DistributorFeed feed)
    {
        return new SettlementService(feed);
    }
}
