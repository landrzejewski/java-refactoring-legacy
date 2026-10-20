namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step1;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 1: bez zmian.</summary>
public static class SettlementModule
{
    public static SettlementService SettlementService(DistributorFeed feed)
    {
        return new SettlementService(feed);
    }
}
