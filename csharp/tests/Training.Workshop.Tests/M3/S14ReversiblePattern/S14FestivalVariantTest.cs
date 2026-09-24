using Training.Workshop.M3.S14ReversiblePattern;

namespace Training.Workshop.Tests.M3.S14ReversiblePattern;

/// <summary>
/// Wariant festiwalowy (zmiana zachowania, więc bez Start - ten jest edytowany na żywo):
/// w kroku 1 obsługuje go adapter, od kroku 2 umowy festiwalowe są odrzucane.
/// </summary>
public sealed class S14FestivalVariantTest
{
    private static readonly Deal Festival = new("Amator", "FESTIVAL");
    private const decimal Revenue = 3000.00m;

    [Fact]
    public void Step1AdapterPaysTheFestivalFeeConvertedFromCents()
    {
        var step1 = new Training.Workshop.M3.S14ReversiblePattern.Step1.DistributorSettlement();
        Assert.Equal(300.00m, step1.Payout(Festival, 1, Revenue));
        Assert.Equal(150.00m, step1.Payout(Festival, 3, Revenue));
    }

    [Fact]
    public void AfterTheVariantIsGoneFestivalDealsAreRejected()
    {
        Assert.Throws<ArgumentException>(() => new Training.Workshop.M3.S14ReversiblePattern.Step2
            .DistributorSettlement().Payout(Festival, 1, Revenue));
        Assert.Throws<ArgumentException>(() => new Training.Workshop.M3.S14ReversiblePattern.Step3
            .DistributorSettlement().Payout(Festival, 1, Revenue));
    }
}
