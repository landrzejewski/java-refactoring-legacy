using Training.Workshop.M8.S01BranchByAbstraction;
using Training.Workshop.M8.S01BranchByAbstraction.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M8.S01BranchByAbstraction;

/// <summary>Wspólny test kontraktowy obu implementacji za abstrakcją i dowód zamknięcia migracji.</summary>
public sealed class S01SolutionTest
{
    private readonly IReadOnlyList<BookingRequest> _requests =
    [
        new BookingRequest(S01EquivalenceTest.Diuna, ["A5", "A10"], ["N", "S"], true, false),
        new BookingRequest(S01EquivalenceTest.KrainaLodu, ["B1", "B2"], ["C", "N"], false, false),
        new BookingRequest(S01EquivalenceTest.Amator, S01EquivalenceTest.GroupSeats,
            Enumerable.Repeat("S", 10).ToList(), false, false),
        new BookingRequest(S01EquivalenceTest.AmatorRano, ["C12", "C13"], ["E", "C"], true, false),
    ];

    [Fact]
    public void LegacyAndModernPricingFulfilTheSameContract()
    {
        ITicketPricing legacy = new LegacyTicketPricing();
        ITicketPricing modern = new ModernTicketPricing();
        foreach (var request in _requests)
        {
            Assert.Equal(legacy.Total(request), modern.Total(request));
        }
    }

    [Fact]
    public void ModernPricingUsesMoneyWithScaleTwo()
    {
        Money total = new ModernTicketPricing().Total(_requests[2]);
        // 10 x (25 - 25%) = 187.50, grupa -10% = 168.75
        Assert.Equal(Money.Of("168.75"), total);
    }

    [Fact]
    public void MigrationIsClosedOnlyWhenTheOldPathIsGone()
    {
        var assembly = typeof(Training.Workshop.M8.S01BranchByAbstraction.Step4.BookingService).Assembly;
        const string step4 = "Training.Workshop.M8.S01BranchByAbstraction.Step4.";
        Assert.Null(assembly.GetType(step4 + "LegacyTicketPricing"));
        Assert.Null(assembly.GetType(step4 + "PricingMode"));
    }
}
