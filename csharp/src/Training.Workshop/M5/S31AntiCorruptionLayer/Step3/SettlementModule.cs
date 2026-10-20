namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step3;

/// <summary>Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: domena dostaje licencje przez ACL.</summary>
public static class SettlementModule
{
    public static SettlementService SettlementService(DistributorFeed feed)
    {
        return new SettlementService(new DistributorAcl(feed));
    }
}
