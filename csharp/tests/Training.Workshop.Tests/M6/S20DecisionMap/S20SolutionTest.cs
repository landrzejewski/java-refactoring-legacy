using Training.Workshop.M6.S20DecisionMap.Step2;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S20DecisionMap;

/// <summary>Kryterium wyboru: która zmiana jest tania w danej strukturze.</summary>
public sealed class S20SolutionTest
{
    [Fact]
    public void PathAMakesANewDayCampaignCheap()
    {
        DayPolicy seniorWednesday = basePrice => basePrice.Minus(Money.Of("5.00"));
        var pricing = new ShowPricing(day => day == DayOfWeek.Wednesday
            ? seniorWednesday : DayPolicies.Standard(day));
        Assert.Equal(Money.Of("35.00"), pricing.Price(DayOfWeek.Wednesday, "IMAX"));
        Assert.Equal(Money.Of("28.00"), pricing.Price(DayOfWeek.Tuesday, "IMAX"));
    }

    [Fact]
    public void PathBKeepsEverythingAboutAFormatInOnePlace()
    {
        var imax = Training.Workshop.M6.S20DecisionMap.Step3.Format.Of("IMAX");
        Assert.Equal(Money.Of("42.00"), imax.PriceOn(DayOfWeek.Saturday));
    }
}
